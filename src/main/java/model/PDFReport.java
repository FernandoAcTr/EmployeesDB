package model;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import javafx.scene.text.Font;
import sun.font.FontFamily;

import java.io.*;
import java.util.List;
import java.util.StringTokenizer;

public class PDFReport {

    private File file;
    private PdfWriter writer;
    private PdfDocument pdf;
    private Document document;

    private PdfFont font;
    private PdfFont bold;

    public PDFReport(String dest, String titleDocument) throws IOException {

        //create a file if it doesn't exist
        file = new File(dest);
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        if(!file.exists())
            file.createNewFile();

        //Initialize PDF writer
        writer = new PdfWriter(file);

        //Initialize PDF document
        pdf = new PdfDocument(writer);

        // Initialize document
        document = new Document(pdf, PageSize.A4.rotate());
        document.setMargins(20, 20, 20, 20);

        font = PdfFontFactory.createFont(FontConstants.HELVETICA);
        bold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);

        //create a TitleDocument
        Paragraph title = new Paragraph(titleDocument);
        title.setFont(bold);
        title.setFontSize(18);
        title.setTextAlignment(TextAlignment.CENTER);
        document.add(title);
    }

    public void addTable(String[] headers, String[][] values) throws IOException {

        //establish the columns size
        float columns[] = new float[headers.length];

        for (int i = 0; i < headers.length; i++)
            columns[i] = 4;

        Table table = new Table(columns);
        table.setWidth(UnitValue.createPercentValue(100));

        processHeaders(table, headers, bold);
        processValues(table, values, font);

        document.add(table);
    }

    private void processValues(Table table, String[][] values, PdfFont font) {
        for (String[] rowValues : values)
            for(String value : rowValues){
                table.addCell(new Cell().add(new Paragraph(value).setFont(font)));
            }
    }

    private void processHeaders(Table table, String[] headers, PdfFont font){

        for (String header : headers)
            table.addHeaderCell(new Cell().add(new Paragraph(header)).setFont(font));

    }

    public void addTitle(String title){
        //create a TitleDocument
        Paragraph t = new Paragraph(title);
        t.setFont(bold);
        t.setFontSize(18);
        t.setTextAlignment(TextAlignment.CENTER);
        document.add(t);
    }

    public void addSubtitle(String subtitle){
        //create a TitleDocument
        Paragraph sub = new Paragraph(subtitle);
        sub.setFont(bold);
        sub.setFontSize(15);
        sub.setTextAlignment(TextAlignment.LEFT);
        document.add(sub);
    }

    public void addParagraph(String text){
        //create a TitleDocument
        Paragraph p = new Paragraph(text);
        p.setFont(font);
        p.setFontSize(12);
        p.setTextAlignment(TextAlignment.LEFT);
        document.add(p);
    }

    public void addBlankLine(){
        //create a TitleDocument
        Paragraph p = new Paragraph("\n");
        p.setFont(font);
        p.setFontSize(12);
        p.setTextAlignment(TextAlignment.LEFT);
        document.add(p);
    }

    public void closeDocument(){
        //Close document
        document.close();
    }
}
