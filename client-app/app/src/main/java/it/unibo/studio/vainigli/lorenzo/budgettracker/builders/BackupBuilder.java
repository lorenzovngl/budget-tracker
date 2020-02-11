package it.unibo.studio.vainigli.lorenzo.budgettracker.builders;

import android.content.Context;
import android.content.res.Resources;

import java.io.File;
import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Register;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.FileUtils;

public class BackupBuilder {

    public BackupBuilder(Context context) {
        final String DATA_DIR = context.getApplicationInfo().dataDir;
        List<Register> registerList = FileUtils.getFileList(context, Register.class);
        String dstFolder = DATA_DIR + "/backups";
        for (Register register : registerList){
            FileUtils.copy(context, register.getPath(), dstFolder + "/databases", register.getName());
        }
        String fileName = "Backup" + DateUtils.getDateFromFormat("ddMMyyyyhhmmss") + ".bk";
        FileUtils.compress(context, dstFolder, dstFolder + "/" + fileName);
        FileUtils.deleteRecursive(new File(dstFolder + "/databases"));
    }

}
