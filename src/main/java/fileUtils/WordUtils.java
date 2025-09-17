package fileUtils;

import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.*;

/**
 * Class holds methods to work with Word files (.docx)
 */
@SuppressWarnings("unused")
public class WordUtils {

    private static final Logger logger = LoggerFactory.getLogger(WordUtils.class);

    /**
     * Return all lines of text from Word file as a list of strings
     * @param filePath file path
     * @return all lines of text from Word file as a list of strings
     * @author reed.dakota
     * @since 26.08.2025
     */
    public static List<String> parseWordFile(String filePath) throws Exception {
        List<String> lines = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis)) {

            // get paragraphs
            for (XWPFParagraph para : document.getParagraphs()) {
                String text = para.getText().trim();
                if (!text.isEmpty()) {
                    lines.add(text);
                }
            }

            // also check tables (sometimes data is only inside tables)
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        String text = cell.getText().trim();
                        if (!text.isEmpty()) {
                            lines.add(text);
                        }
                    }
                }
            }
        }

        return lines;
    }

}
