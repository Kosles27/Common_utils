package fileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class holds methods to work with Txt files
 */
public class TxtUtils {

    private static Logger logger= LoggerFactory.getLogger(TxtUtils.class);

    /**
     * get the content of Txt file
     * @param fileName - file name to get its content
     * @return the content of Txt file as List<String>
     * @since 02.04.2023
     * @author abo_saleh.rawand
     */
    public static List<String> readTxtFile(String fileName) {
        List<String> txtContent = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            txtContent = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                txtContent.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return txtContent;
    }
}
