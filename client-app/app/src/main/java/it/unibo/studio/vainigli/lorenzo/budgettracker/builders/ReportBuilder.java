package it.unibo.studio.vainigli.lorenzo.budgettracker.builders;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.DoubleAccumulator;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoMovements;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoRegisters;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.MdaoMovements;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.RegistrersController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Movement;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Register;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.FileUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.FiltersController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.NumberUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.Statistic;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.StringUtils;

public class ReportBuilder {

    public static final File DOWNLOADS_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    public ReportBuilder(Context context) {
        final String DATA_DIR = context.getApplicationInfo().dataDir;
        Resources res = context.getResources();
        try {
            //File pdfFolder = createDirectory(DOWNLOADS_DIR + "/reports");
            File pdfFolder = FileUtils.createDirectory(DATA_DIR + "/reports");
            File myFile = new File(pdfFolder.getAbsolutePath(), "Report" + DateUtils.getDateFromFormat("ddMMyyhhmm") + ".pdf");
            //Log.i("PDF file", myFile.getAbsolutePath());
            if (!myFile.exists()) {
                try {
                    myFile.createNewFile();
                    //Log.i("PDFi", "Pdf File created");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            myFile.setReadable(true, false);
            myFile.setWritable(true, false);
            try {
                OutputStream output = new FileOutputStream(myFile);
                Document document = new Document();
                PdfWriter pdfWriter = PdfWriter.getInstance(document, output);
                document.open();
                // Get active filters
                FiltersController filtersController = new FiltersController(context);
                Set<String> categories = filtersController.getSrcCategories();
                double startAmount = filtersController.getStartAmount();
                double endAmount = filtersController.getEndAmount();
                String startDate = filtersController.getStartDate();
                String endDate = filtersController.getEndDate();
                // Fonts
                Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 28, Font.BOLD);
                Font tableNameFont = new Font(Font.FontFamily.TIMES_ROMAN, 18);
                Font bodyFont = new Font(Font.FontFamily.TIMES_ROMAN, 14);
                Font bodyFontBold = new Font(bodyFont.getFamily(), bodyFont.getSize(), Font.BOLD);
                // Table elements
                PdfPCell cell1 = new PdfPCell(new Paragraph(res.getString(R.string.date), bodyFontBold));
                PdfPCell cell2 = new PdfPCell(new Paragraph(res.getString(R.string.description), bodyFontBold));
                PdfPCell cell3 = new PdfPCell(new Paragraph("Fonte", bodyFontBold));
                PdfPCell cell4 = new PdfPCell(new Paragraph("Destinazione", bodyFontBold));
                PdfPCell cell5 = new PdfPCell(new Paragraph(res.getString(R.string.amount), bodyFontBold));
                // Table elements formatting
                cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
                // Document Header
                try {
                    InputStream ims = context.getAssets().open("ic_launcher-web.png");
                    Bitmap bmp = BitmapFactory.decodeStream(ims);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image image = Image.getInstance(stream.toByteArray());
                    image.scaleAbsolute(100, 100);
                    document.add(image);
                } catch (IOException e){
                    e.printStackTrace();
                }
                Paragraph header = new Paragraph(res.getString(R.string.report_of_movements), titleFont);
                header.setAlignment(Element.ALIGN_CENTER);
                document.add(header);
                // Table elements describing movements
                MdaoMovements mdaoMovements = new MdaoMovements(Const.DBMode.READ, context);
                for (DaoMovements daoMovements : mdaoMovements.getDaos()){
                    Paragraph tableHeader = new Paragraph("Registro " + daoMovements.getDatabaseName(), tableNameFont);
                    document.add(tableHeader);
                    PdfPTable table = new PdfPTable(5); // 5 columns.
                    // Table formatting
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(10f);
                    table.setSpacingAfter(10f);
                    // Adding table elements
                    table.addCell(cell1);
                    table.addCell(cell2);
                    table.addCell(cell3);
                    table.addCell(cell4);
                    table.addCell(cell5);
                    List<Movement> movements = daoMovements.getFilteredData(null, RegistrersController.COL_DATE, Const.ORDER_ASC);
                    for (Movement movement : movements) {
                        PdfPCell cellDate = new PdfPCell(new Paragraph(movement.getStringDate(), bodyFont));
                        PdfPCell cellDesc = new PdfPCell(new Paragraph(movement.getDescription(), bodyFont));
                        PdfPCell cellSrcCat = new PdfPCell(new Paragraph(movement.getSrcCategory(), bodyFont));
                        PdfPCell cellDstCat = new PdfPCell(new Paragraph(movement.getDstCategory(), bodyFont));
                        String amountString = NumberUtils.doubleToCurrency(movement.getAmount(), true);
                        PdfPCell cellAmount = new PdfPCell(new Paragraph(amountString, bodyFont));
                        cellAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(cellDate);
                        table.addCell(cellDesc);
                        table.addCell(cellSrcCat);
                        table.addCell(cellDstCat);
                        table.addCell(cellAmount);
                    }
                    document.add(table);
                }
                if (filtersController.isEnabled()) {
                    if (filtersController.isEnabledSrcCategories()) {
                        List<String> categoriesList = StringUtils.setToList(categories);
                        //Log.i("STAMPA", StringUtils.printStringList(categoriesList));
                        if (categoriesList != null && categoriesList.size() > 0) {
                            String categoriesString = "";
                            for (int i = 0; i < categoriesList.size(); i++) {
                                if (i == 0) {
                                    categoriesString = categoriesString.concat(res.getString(R.string.categories) + ": ");
                                } else {
                                    categoriesString = categoriesString.concat(", ");
                                }
                                categoriesString = categoriesString.concat(categoriesList.get(i));
                            }
                            //Log.i("STAMPA", categoriesString);
                            document.add(new Paragraph(categoriesString + ".", bodyFont));
                        }
                    }
                    if (filtersController.isEnabledDate()) {
                        String startDateString = null;
                        String endDateString = null;
                        if (startDate != null || endDate != null) {
                            if (startDate != null && endDate != null) {
                                // Caso 1: date entrambe valide
                                startDateString = filtersController.getStartDate();
                                endDateString = filtersController.getEndDate();
                            } else if (startDate == null) {
                                // Caso 2: data di inizio non valida
                                startDateString = DateUtils.dateToString(mdaoMovements.getFirstDate(), DateUtils.FORMAT_IT);
                                endDateString = filtersController.getEndDate();
                            } else {
                                // Caso 3: data di fine non valida
                                startDateString = filtersController.getStartDate();
                                endDateString = DateUtils.dateToString(new Date(), DateUtils.FORMAT_IT);
                            }
                            document.add(new Paragraph(res.getString(R.string.period) + ": " + startDateString + " - " + endDateString, bodyFont));
                        }
                    }
                    if (filtersController.isEnabledAmount()) {
                        String startAmountString = null;
                        String endAmountString = null;
                        if (startAmount > 0 || endAmount > 0) {
                            if (startAmount > 0 && endAmount > 0) {
                                // Caso 1: importi entrambi validi
                                startAmountString = NumberUtils.doubleToCurrency(filtersController.getStartAmount(), true);
                                endAmountString = NumberUtils.doubleToCurrency(filtersController.getEndAmount(), true);
                                document.add(new Paragraph(res.getString(R.string.range) + ": " + startAmountString + " - " + endAmountString + " €", bodyFont));
                            } else if (startAmount <= 0) {
                                // Caso 2: importo di inizio non valido
                                startAmountString = "0 €";
                                endAmountString = NumberUtils.doubleToCurrency(filtersController.getEndAmount(), true);
                                document.add(new Paragraph(res.getString(R.string.range) + ": " + startAmountString + " - " + endAmountString + " €", bodyFont));
                            } else {
                                // Caso 2: importo di fine non valido
                                startAmountString = NumberUtils.doubleToCurrency(filtersController.getStartAmount(), true);
                                document.add(new Paragraph(res.getString(R.string.range) + ": " + res.getString(R.string._from) + " " + startAmountString + " €", bodyFont));
                            }
                        }
                    }
                }
                Date today = new Date();
                double totalIncomings = mdaoMovements.getTotal(Const.MovementType.INCOME, Const.Period.ALLTIME, today);
                double currentIncomings = mdaoMovements.getTotal(Const.MovementType.INCOME, Const.Period.CURRENT, today);
                double futureIncomings = totalIncomings - currentIncomings;
                double totalExpenses = mdaoMovements.getTotal(Const.MovementType.EXPENSE, Const.Period.ALLTIME, today);
                double currentExpenses = mdaoMovements.getTotal(Const.MovementType.EXPENSE, Const.Period.CURRENT, today);
                double futureExpenses = totalExpenses - currentExpenses;
                double totalDiff = mdaoMovements.getTotal(Const.MovementType.ALL, Const.Period.ALLTIME, today);
                double currentDiff = mdaoMovements.getTotal(Const.MovementType.ALL, Const.Period.CURRENT, today);
                double futureDiff = totalDiff - currentDiff;
                document.add(new Paragraph(res.getString(R.string.total_incomings) + ": " + NumberUtils.doubleToCurrency(totalIncomings, true), bodyFont));
                document.add(new Paragraph(res.getString(R.string.current_incomings) + ": " + NumberUtils.doubleToCurrency(currentIncomings, true), bodyFont));
                document.add(new Paragraph(res.getString(R.string.future_incomings) + ": " + NumberUtils.doubleToCurrency(futureIncomings, true), bodyFont));
                document.add(new Paragraph(res.getString(R.string.total_expenses) + ": " + NumberUtils.doubleToCurrency(totalExpenses, true), bodyFont));
                document.add(new Paragraph(res.getString(R.string.current_expenses) + ": " + NumberUtils.doubleToCurrency(currentExpenses, true), bodyFont));
                document.add(new Paragraph(res.getString(R.string.future_expenses) + ": " + NumberUtils.doubleToCurrency(futureExpenses, true), bodyFont));
                document.add(new Paragraph(res.getString(R.string.difference) + ": " + NumberUtils.doubleToCurrency(totalDiff, true), bodyFont));
                document.add(new Paragraph(res.getString(R.string.current_difference) + ": " + NumberUtils.doubleToCurrency(currentDiff, true), bodyFont));
                document.add(new Paragraph(res.getString(R.string.future_difference) + ": " + NumberUtils.doubleToCurrency(futureDiff, true), bodyFont));
                document.close();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
