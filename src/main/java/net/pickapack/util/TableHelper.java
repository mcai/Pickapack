package net.pickapack.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Min Cai
 */
public class TableHelper {
    private static final Font boldFont = new Font(Font.FontFamily.HELVETICA, Font.DEFAULTSIZE, Font.BOLD);
    private static final Font normalFont = new Font(Font.FontFamily.HELVETICA, Font.DEFAULTSIZE, Font.NORMAL);
    private static final Rectangle A3_LANDSCAPE = new RectangleReadOnly(842,1191, 90);
    private static final Rectangle A3_EXTRA_LANDSCAPE = new RectangleReadOnly(842,1391, 90);

    /**
     *
     * @param fileName
     * @param columns
     * @param rows
     */
    public static void generateTable(String fileName, List<String> columns, List<List<String>> rows) {
        Document document = new Document(A3_EXTRA_LANDSCAPE);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            PdfPTable table = new PdfPTable(columns.size());
            table.setWidthPercentage(100.0f);

            addRow(table, true, columns);

            for (List<String> row : rows) {
                addRow(table, false, row);
            }

            document.add(table);

            table.setHeaderRows(0);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addRow(PdfPTable table, boolean header, List<String> values) {
        List<PdfPCell> cells = new ArrayList<PdfPCell>();

        for (Object value : values) {
            PdfPCell cell = new PdfPCell(new Phrase(value + "", header ? boldFont : normalFont));
            if (header) {
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            }

            cells.add(cell);
        }

        table.getRows().add(new PdfPRow(cells.toArray(new PdfPCell[cells.size()])));
    }
}