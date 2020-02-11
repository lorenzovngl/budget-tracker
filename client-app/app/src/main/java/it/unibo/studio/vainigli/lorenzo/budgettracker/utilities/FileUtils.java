package it.unibo.studio.vainigli.lorenzo.budgettracker.utilities;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.MultiPartRequest;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.UsersPrefsController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Backup;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Register;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Report;

public class FileUtils {

    public static String BACKUPS_PATH = "/backups/";
    public static String DATABASE_PATH = "/databases/";
    public static String REPORTS_PATH = "/reports/";
    public static String SHARED_PREFS_PATH = "/shared_prefs/";
    private static final int BUFFER_SIZE = 2048;

    public static <T> String getPath(Context context, Class<T> className){
        String path = context.getApplicationInfo().dataDir;
        if (className.getName().equals(Backup.class.getName())){
            path += BACKUPS_PATH;
        } else if (className.getName().equals(Register.class.getName())){
            path += DATABASE_PATH;
        } else if (className.getName().equals(Report.class.getName())){
            path += REPORTS_PATH;
        }
        return path;
    }

    public static File createDirectory(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            Boolean res = folder.mkdir();
            /*if (res)
                Log.i("PDFi", "Directory created");
            else
                Log.i("PDFi", "Fail to create dir");*/
        }
        folder.setReadable(true, false);
        folder.setWritable(true, false);
        Log.i("PDFi", Boolean.toString(folder.canRead()));
        Log.i("PDFi", Boolean.toString(folder.canWrite()));
        return folder;
    }

    // http://stackoverflow.com/questions/9292954/how-to-make-a-copy-of-a-file-in-android
    // Modificata
    public static File copy(Context context, File src, File dst) {
        if (!dst.exists()) {
            try {
                dst.createNewFile();
                //Log.i("PDFi", "Pdf File created in Download");
            } catch (IOException e) {
                //Log.i("PDFi", "Pdf File NOT created in Download");
                e.printStackTrace();
            }
        }
        try {
            FileInputStream inStream = new FileInputStream(src);
            FileOutputStream outStream = new FileOutputStream(dst);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtils.showSimpleDialog(context, "Errore", "Impossibile aprire il file");
        }
        dst.setReadable(true, false);
        dst.setWritable(true, false);
        dst.setExecutable(true, false);
        //Log.i("PDFi", Boolean.toString(dst.canRead()));
        //Log.i("PDFi", Boolean.toString(dst.canWrite()));
        return dst;
    }

    public static File copy(Context context, String srcFile, String dstFolder, String dstFile) {
        File src = new File(srcFile);
        File folder = FileUtils.createDirectory(dstFolder);
        File dst = new File(folder.getAbsolutePath(), dstFile);
        return copy(context, src, dst);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getFileList(Context context, Class<T> className) {
        String folderPath = getPath(context, className);
        List<T> list = new ArrayList<>();
        File f = new File(folderPath);
        if (!f.exists()) {
            f = FileUtils.createDirectory(folderPath);
        }
        for (File file : f.listFiles()) {
            if (className.getName().equals(Backup.class.getName())) {
                Backup backup = new Backup();
                backup.setDescription(file.getName());
                backup.setDate(new Date(file.lastModified()));
                backup.setPath(folderPath + file.getName());
                list.add((T) backup);
            } else if (className.getName().equals(Report.class.getName())) {
                Report report = new Report();
                report.setDescription(file.getName());
                report.setDate(new Date(file.lastModified()));
                report.setPath(folderPath + file.getName());
                list.add((T) report);
            } else if (className.getName().equals(Register.class.getName())) {
                // I want only .db files
                Pattern pattern = Pattern.compile("(" + UsersPrefsController.getUsername(context) + "_\\w+.db)$");
                Log.i("PATTERN", "(" + UsersPrefsController.getUsername(context) + "_\\w+.db)$");
                Matcher matcher = pattern.matcher(file.getName());
                if (matcher.find()) {
                    Register database = new Register();
                    database.setName(file.getName().substring(file.getName().indexOf("_") + 1));
                    Log.i("FILE NAME", file.getName().substring(file.getName().indexOf("_") + 1));
                    database.setSize(file.length());
                    database.setPath(folderPath + file.getName());
                    list.add((T) database);
                    Log.i("MATCH_GROUP", matcher.group() + " end");
                }
            }
        }
        return list;
    }

    // http://thunderingtyfoons.blogspot.it/2013/06/compressing-decompressing-files-using.html
    public static void compress(Context context, String inputFile, String compressedFile) {
        try {
            ZipFile zipFile = new ZipFile(compressedFile);
            File inputFileH = new File(inputFile);
            File outputFileH = new File(compressedFile);
            ZipParameters parameters = new ZipParameters();
            // COMP_DEFLATE is for compression
            // COMP_STORE no compression
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            // DEFLATE_LEVEL_ULTRA = maximum compression
            // DEFLATE_LEVEL_MAXIMUM
            // DEFLATE_LEVEL_NORMAL = normal compression
            // DEFLATE_LEVEL_FAST
            // DEFLATE_LEVEL_FASTEST = fastest compression
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
            // file compressed
            zipFile.addFolder(inputFileH, parameters);
            long uncompressedSize = inputFileH.length();
            long comrpessedSize = outputFileH.length();
            //System.out.println("Size "+uncompressedSize+" vs "+comrpessedSize);
            double ratio = (double)comrpessedSize/(double)uncompressedSize;
            DialogUtils.showSimpleDialog(context, "Successo", "Archivio creato.");
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showSimpleDialog(context, "Errore", "Impossibile comprimere il file.");
        }
    }

    public static void decompress(Context context, String compressedFile, String destination) {
        try {
            ZipFile zipFile = new ZipFile(compressedFile);
            if (zipFile.isEncrypted()) {
                //zipFile.setPassword(password);
            }
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        }
        //DialogUtils.showSimpleDialog(context, "Successo", "Archivio decompresso.");
    }

    // TODO da rimuovere
    public static void downloadFile(final ServerController serverController, final Context context, final String url, final String path) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        // Response Listener
        final Response.Listener<File> responseListener = new Response.Listener<File>() {
            @Override
            public void onResponse(File response) {
                DialogUtils.showSimpleDialog(context, "Successo", "File scaricato!");
                serverController.syncRemoteFinish();
            }
        };
        // Response Error Listener
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.showSimpleDialog(context, "Errore", "Impossibile connettersi al server.");
            }
        };

        // Request a string response from the provided URL.
        MultiPartRequest<File> multiPartRequest = new MultiPartRequest<File>(Request.Method.GET, url, responseListener, errorListener) {
            @Override
            protected Response<File> parseNetworkResponse(NetworkResponse response) {
                try {
                    byte[] bytes = response.data;
                    File outfile = new File(path);
                    FileOutputStream fos = new FileOutputStream(outfile);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bos.write(bytes);
                    bos.flush();
                    bos.close();
                    return Response.success(outfile, HttpHeaderParser.parseCacheHeaders(response));
                } catch (FileNotFoundException e){
                    return Response.error(new VolleyError(response));
                } catch (IOException e){
                    return Response.error(new VolleyError(response));
                }
            }
        };
        // Add the request to the RequestQueue.
        queue.add(multiPartRequest);
    }

    public static void downloadFile(final Context context, final String url, final String path) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        // Response Listener
        final Response.Listener<File> responseListener = new Response.Listener<File>() {
            @Override
            public void onResponse(File response) {
                DialogUtils.showSimpleDialog(context, "Successo", "File scaricato!");
            }
        };
        // Response Error Listener
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                DialogUtils.showSimpleDialog(context, "Errore", "Impossibile connettersi al server.");
            }
        };
        // Request a string response from the provided URL.
        MultiPartRequest<File> multiPartRequest = new MultiPartRequest<File>(Request.Method.GET, url, responseListener, errorListener) {
            @Override
            protected Response<File> parseNetworkResponse(NetworkResponse response) {
                try {
                    byte[] bytes = response.data;
                    File outfile = new File(path);
                    FileOutputStream fos = new FileOutputStream(outfile);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bos.write(bytes);
                    bos.flush();
                    bos.close();
                    return Response.success(outfile, HttpHeaderParser.parseCacheHeaders(response));
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                    return Response.error(new VolleyError(response));
                } catch (IOException e){
                    return Response.error(new VolleyError(response));
                }
            }
        };
        // Add the request to the RequestQueue.
        queue.add(multiPartRequest);
    }

    // TODO da rimuovere in favore di quella che usa l'oggetto Register
    public static List<String> getDatabasesList(Context context){
        final String DATA_DIR = context.getApplicationInfo().dataDir;
        String DB_DIR = DATA_DIR + "/databases";
        Log.i("PATH", DB_DIR);
        File databasesDir = new File(DB_DIR);
        List<String> fileNames = new ArrayList<String>();
        for (File file : databasesDir.listFiles()){
            fileNames.add(file.getName());
        }
        Pattern pattern = Pattern.compile("(\\w+.db)$");
        List<String> matchedFileNames = new ArrayList<String>();
        for (String string : fileNames){
            Matcher matcher = pattern.matcher(string);
            if (matcher.find()){
                matchedFileNames.add(string);
                Log.i("MATCH_GROUP", matcher.group() + " end");
            }
        }
        return matchedFileNames;
    }

    public static List<Register> getDatabases(Context context){
        final String DATA_DIR = context.getApplicationInfo().dataDir;
        String DB_DIR = DATA_DIR + "/databases";
        Log.i("PATH", DB_DIR);
        File databasesDir = new File(DB_DIR);
        List<String> fileNames = new ArrayList<>();
        for (File file : databasesDir.listFiles()){
            fileNames.add(file.getName());
        }
        Pattern pattern = Pattern.compile("(\\w+.db)$");
        List<Register> matchedFileNames = new ArrayList<>();
        for (String string : fileNames){
            Matcher matcher = pattern.matcher(string);
            if (matcher.find()){
                Register database = new Register();
                database.setName(string);
                matchedFileNames.add(database);
                Log.i("MATCH_GROUP", matcher.group() + " end");
            }
        }
        return matchedFileNames;
    }

    public static void deleteDatabases(Context context){
        final String DATA_DIR = context.getApplicationInfo().dataDir;
        String DB_DIR = DATA_DIR + "/databases";
        File databasesDir = new File(DB_DIR);
        for (File file : databasesDir.listFiles()){
            if (!file.getName().equals(Const.DATABASES_INFO)){
                file.delete();
            }
        }
    }

    public static String newDatabaseName(Context context, String name){
        return UsersPrefsController.getUsername(context) + "_" + name;
    }

    public static String newDatabasePath(Context context, String name){
        return getPath(context, Register.class) + newDatabaseName(context, name);
    }

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()){
            for (File child : fileOrDirectory.listFiles()){
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }

}
