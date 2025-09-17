package fileUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tableUtils.Table;
import tableUtils.TableCell;
import tableUtils.TableRow;
import tableUtils.TrapRangeBuilder;

import java.io.*;
import java.util.*;

public class PdfUtils {

    private static Logger logger= LoggerFactory.getLogger(PdfUtils.class);

    //extracted pages of file
    private static final List<Integer> extractedPages = new ArrayList<>();
    private static final List<Integer> exceptedPages = new ArrayList<>();
    //contains avoided line idx-s for each page
    private static final Multimap<Integer, Integer> pageNExceptedLinesMap = HashMultimap.create();


    private static PDDocument document;

    /**
     * Get a PDF file as a java.io.file
     * @param filePath path to the pdf
     * @return a PDF file as a java.io.file
     */
    public static File getPdfFile(String filePath)
    {
        //Load the Pdf file
        return new File(filePath);
    }

    /**
     * Convert a pdf to text
     * @return the pdf content as text
     * @throws IOException
     * @author Dafna Genosae
     * @since 24.01.2022
     */
    public static String generateTextFromPDF(File file) throws IOException {

        String parsedText;
        PDFParser parser = new PDFParser(new RandomAccessFile(file, "r"));
        parser.parse();

        //Extract the text
        COSDocument cosDoc = parser.getDocument();
        PDFTextStripper pdfStripper = new PDFTextStripper();
        PDDocument pdDoc = new PDDocument(cosDoc);
        parsedText = pdfStripper.getText(pdDoc);
        return parsedText;
    }

    /**
     * Convert a pdf to text
     * @param filePath path to pdf file
     * @return the pdf content as text
     * @throws IOException
     * @author Dafna Genosae
     * @since 24.01.2022
     */
    public static String generateTextFromPDF(String filePath) throws IOException {

        //Load the Pdf file
        File file = getPdfFile(filePath);
        return generateTextFromPDF(file);
    }

    /**
     * Convert the last modified pdf in Downloads folder to text
     * @return the pdf content as text
     * @throws IOException
     * @author Dafna Genosae
     * @since 24.01.2022
     */
    public static String generateTextFromLastModifiedPDF() throws IOException {

        String pdfFilePath = FileUtils.getLastFileFromDownloads();
        return generateTextFromPDF(pdfFilePath);
    }

    /**
     * get the line number where the text located in the file
     * @param extractedTables - the extracted file content
     * @param pageIdx - page index to search the text in it
     * @param text - the value to search for in the page
     * @return - if the text found return number of line else return -1
     * @since 15.02.2023
     * @author abo_saleh.rawand
     */
    public static int getLineNumberOfString(List<Table> extractedTables, int pageIdx, String text) {
        List<TableRow> rows= extractedTables.get(pageIdx).getRows();
        int NumberOfRows = extractedTables.get(pageIdx).getRows().size();
        for(int i =0 ; i<NumberOfRows;i++){
            String wordFounded = rows.get(i).getCells().get(0).getContent();
            if(wordFounded.contains(text)){
                return i;
            }
        }
        return -1;
    }


    /**
     * extract specific row in the PDF file by its number
     * @param tables - extracted tables in PDF file
     * @param pageIdx - page number
     * @param rowIdx - row number
     * @return - extracted row of PDF file
     * @since 13.02.2023
     * @author abo_saleh.rawand
     */

    public static String extractSpecificRow(List<Table> tables,int pageIdx,int rowIdx){
        List<TableRow> rows= tables.get(pageIdx).getRows();
        return rows.get(rowIdx).getCells().get(0).getContent();
    }

    /**
     * extract Tables of PDF file
     * @param filePath - PDF file path to Extract Tables
     * @return - List of extracted tables
     */
    public static List<Table> extract(String filePath) {
        List<Table> retVal = new ArrayList<>();
        Multimap<Integer, Range<Integer>> pageIdNLineRangesMap = LinkedListMultimap.create();
        Multimap<Integer, TextPosition> pageIdNTextsMap = LinkedListMultimap.create();
        try {
            File newFile = new File(filePath);
            document = PDDocument.load(newFile);
            for (int pageId = 0; pageId < document.getNumberOfPages(); pageId++) {
                boolean b = !exceptedPages.contains(pageId) && (extractedPages.isEmpty() || extractedPages.contains(pageId));
                if (b) {
                    List<TextPosition> texts = extractTextPositions(pageId);//sorted by .getY() ASC
                    //extract line ranges
                    List<Range<Integer>> lineRanges = getLineRanges(pageId, texts);
                    //extract column ranges
                    List<TextPosition> textsByLineRanges = getTextsByLineRanges(lineRanges, texts);

                    pageIdNLineRangesMap.putAll(pageId, lineRanges);
                    pageIdNTextsMap.putAll(pageId, textsByLineRanges);
                }
            }
            //Calculate columnRanges
            List<Range<Integer>> columnRanges = getColumnRanges(pageIdNTextsMap.values());
            for (int pageId : pageIdNTextsMap.keySet()) {
                Table table = buildTable(pageId, (List) pageIdNTextsMap.get(pageId), (List) pageIdNLineRangesMap.get(pageId), columnRanges);
                retVal.add(table);
                //debug
                logger.debug("Found " + table.getRows().size() + " row(s) and " + columnRanges.size()
                        + " column(s) of a table in page " + pageId);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Parse pdf file fail", ex);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException ex) {
                    logger.error(null, ex);
                }
            }
        }
        //return
        return retVal;
    }

    /**
     * build the tables of pdf file
     *
     * @param pageIdx - number page to build the table
     * @param tableContent - the table content
     * @param rowTrapRanges - row ranges
     * @param columnTrapRanges - column ranges
     * @return - table
     */
    private static Table buildTable(int pageIdx, List<TextPosition> tableContent,
                                    List<Range<Integer>> rowTrapRanges, List<Range<Integer>> columnTrapRanges) {
        Table retVal = new Table(pageIdx, columnTrapRanges.size());
        int idx = 0;
        int rowIdx = 0;
        List<TextPosition> rowContent = new ArrayList<>();
        while (idx < tableContent.size()) {
            TextPosition textPosition = tableContent.get(idx);
            Range<Integer> rowTrapRange = rowTrapRanges.get(rowIdx);
            Range<Integer> textRange = Range.closed((int) textPosition.getY(),
                    (int) (textPosition.getY() + textPosition.getHeight()));
            if (rowTrapRange.encloses(textRange)) {
                rowContent.add(textPosition);
                idx++;
            } else {
                TableRow row = buildRow(rowIdx, rowContent, columnTrapRanges);
                retVal.getRows().add(row);
                //next row: clear rowContent
                rowContent.clear();
                rowIdx++;
            }
        }
        //last row
        if (!rowContent.isEmpty() && rowIdx < rowTrapRanges.size()) {
            TableRow row = buildRow(rowIdx, rowContent, columnTrapRanges);
            retVal.getRows().add(row);
        }
        //return
        return retVal;
    }

    /**
     *build row of the table
     * @param rowIdx - row number
     * @param rowContent - row Content
     * @param columnTrapRanges - column range
     * @return - a table row
     */
    private static TableRow buildRow(int rowIdx, List<TextPosition> rowContent, List<Range<Integer>> columnTrapRanges) {
        TableRow retVal = new TableRow(rowIdx);
        //Sort rowContent
        Collections.sort(rowContent, new Comparator<TextPosition>() {
            @Override
            public int compare(TextPosition o1, TextPosition o2) {
                int retVal = 0;
                if (o1.getX() < o2.getX()) {
                    retVal = -1;
                } else if (o1.getX() > o2.getX()) {
                    retVal = 1;
                }
                return retVal;
            }
        });
        int idx = 0;
        int columnIdx = 0;
        List<TextPosition> cellContent = new ArrayList<>();
        while (idx < rowContent.size()) {
            TextPosition textPosition = rowContent.get(idx);
            Range<Integer> columnTrapRange = columnTrapRanges.get(columnIdx);
            Range<Integer> textRange = Range.closed((int) textPosition.getX(),
                    (int) (textPosition.getX() + textPosition.getWidth()));
            if (columnTrapRange.encloses(textRange)) {
                cellContent.add(textPosition);
                idx++;
            } else {
                TableCell cell = buildCell(columnIdx, cellContent);
                retVal.getCells().add(cell);
                //next column: clear cell content
                cellContent.clear();
                columnIdx++;
            }
        }
        if (!cellContent.isEmpty() && columnIdx < columnTrapRanges.size()) {
            TableCell cell = buildCell(columnIdx, cellContent);
            retVal.getCells().add(cell);
        }
        //return
        return retVal;
    }

    /**
     * Build cell of row
     * @param columnIdx - column number
     * @param cellContent - cell content to build
     * @return - instance of TableCell
     */
    private static TableCell buildCell(int columnIdx, List<TextPosition> cellContent) {
        Collections.sort(cellContent, new Comparator<TextPosition>() {
            @Override
            public int compare(TextPosition o1, TextPosition o2) {
                int retVal = 0;
                if (o1.getX() < o2.getX()) {
                    retVal = -1;
                } else if (o1.getX() > o2.getX()) {
                    retVal = 1;
                }
                return retVal;
            }
        });
        StringBuilder cellContentBuilder = new StringBuilder();
        for (TextPosition textPosition : cellContent) {
            cellContentBuilder.append(textPosition.getUnicode());
        }
        String cellContentString = cellContentBuilder.toString();
        return new TableCell(columnIdx, cellContentString);
    }

    /**
     *
     * @param pageId - page number
     * @return - text positions
     * @throws IOException
     */
    private static List<TextPosition> extractTextPositions(int pageId) throws IOException {
        TextPositionExtractor extractor = new TextPositionExtractor(document, pageId);
        return extractor.extract();
    }

    /**
     * Excepted lines
     * @param pageIdx - number of page
     * @param lineIdx -number of line
     * @return - true/false
     */
    private static boolean isExceptedLine(int pageIdx, int lineIdx) {
        boolean retVal = pageNExceptedLinesMap.containsEntry(pageIdx, lineIdx)
                || pageNExceptedLinesMap.containsEntry(-1, lineIdx);
        return retVal;
    }

    /**
     * get the text by line Ranges
     * @param lineRanges - ranges of line
     * @param textPositions - text position
     * @return - list of text position
     */
    private static List<TextPosition> getTextsByLineRanges(List<Range<Integer>> lineRanges, List<TextPosition> textPositions) {
        List<TextPosition> retVal = new ArrayList<>();
        int idx = 0;
        int lineIdx = 0;
        while (idx < textPositions.size() && lineIdx < lineRanges.size()) {
            TextPosition textPosition = textPositions.get(idx);
            Range<Integer> textRange = Range.closed((int) textPosition.getY(),
                    (int) (textPosition.getY() + textPosition.getHeight()));
            Range<Integer> lineRange = lineRanges.get(lineIdx);
            if (lineRange.encloses(textRange)) {
                retVal.add(textPosition);
                idx++;
            } else if (lineRange.upperEndpoint() < textRange.lowerEndpoint()) {
                lineIdx++;
            } else {
                idx++;
            }
        }
        //return
        return retVal;
    }

    /**
     * Column Ranges
     * @param texts - a collection of text position
     * @return - column Ranges
     */
    private static List<Range<Integer>> getColumnRanges(Collection<TextPosition> texts) {
        TrapRangeBuilder rangesBuilder = new TrapRangeBuilder();
        for (TextPosition text : texts) {
            Range<Integer> range = Range.closed((int) text.getX(), (int) (text.getX() + text.getWidth()));
            rangesBuilder.addRange(range);
        }
        return rangesBuilder.build();
    }

    /**
     * line Ranges
     * @param pageId - number of page
     * @param pageContent - content of page
     * @return - line ranges
     */
    private static List<Range<Integer>> getLineRanges(int pageId, List<TextPosition> pageContent) {
        TrapRangeBuilder lineTrapRangeBuilder = new TrapRangeBuilder();
        for (TextPosition textPosition : pageContent) {
            Range<Integer> lineRange = Range.closed((int) textPosition.getY(),
                    (int) (textPosition.getY() + textPosition.getHeight()));
            //add to builder
            lineTrapRangeBuilder.addRange(lineRange);
        }
        List<Range<Integer>> lineTrapRanges = lineTrapRangeBuilder.build();
        List<Range<Integer>> retVal = removeExceptedLines(pageId, lineTrapRanges);
        return retVal;
    }

    /**
     * remove the excepted Lines
     * @param pageIdx - number of page
     * @param lineTrapRanges - line Ranges
     * @return - removed excepted lines
     */
    private static List<Range<Integer>> removeExceptedLines(int pageIdx, List<Range<Integer>> lineTrapRanges) {
        List<Range<Integer>> retVal = new ArrayList<>();
        for (int lineIdx = 0; lineIdx < lineTrapRanges.size(); lineIdx++) {
            boolean isExceptedLine = isExceptedLine(pageIdx, lineIdx)
                    || isExceptedLine(pageIdx, lineIdx - lineTrapRanges.size());
            if (!isExceptedLine) {
                retVal.add(lineTrapRanges.get(lineIdx));
            }
        }
        //return
        return retVal;
    }

    /**
     * private class to extract position of text
     */
    private static class TextPositionExtractor extends PDFTextStripper {

        private final List<TextPosition> textPositions = new ArrayList<>();
        private final int pageId;

        private TextPositionExtractor(PDDocument document, int pageId) throws IOException {
            super();
            super.setSortByPosition(true);
            super.document = document;
            this.pageId = pageId;
        }

        public void stripPage(int pageId) throws IOException {
            this.setStartPage(pageId + 1);
            this.setEndPage(pageId + 1);
            try (Writer writer = new OutputStreamWriter(new ByteArrayOutputStream())) {
                writeText(document, writer);
            }
        }

        @Override
        protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
            this.textPositions.addAll(textPositions);
        }

        /**
         * and order by textPosition.getY() ASC
         *
         * @return
         * @throws IOException
         */
        private List<TextPosition> extract() throws IOException {
            this.stripPage(pageId);
            //sort
            Collections.sort(textPositions, new Comparator<TextPosition>() {
                @Override
                public int compare(TextPosition o1, TextPosition o2) {
                    int retVal = 0;
                    if (o1.getY() < o2.getY()) {
                        retVal = -1;
                    } else if (o1.getY() > o2.getY()) {
                        retVal = 1;
                    }
                    return retVal;

                }
            });
            return this.textPositions;
        }
    }
}
