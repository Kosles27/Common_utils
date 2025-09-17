package registryUtils;

import com.hp.lft.common.StreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Class holds methods for the Registry
 * @author genosar.dafna
 * @since 27.06.2023
 */
@SuppressWarnings({"unused", "unchecked"})
public class RegistryUtils {

    private static final Logger logger = LoggerFactory.getLogger(RegistryUtils.class);

    /**
     * Get map of registry ket data that holds its name, type and value
     * @param registryPath the registry path - must be in this format: HKEY_CURRENT_USER\\Control Panel\\International
     * @param registryKey the registry key
     * @return map of registry ket data that holds its name, type and value
     * @author genosar.dafna
     * @since 27.06.2023
     */
    public static Map<RegistryKeyHeadersEnum, ?> getRegistryKeyData(String registryPath, String registryKey) {
        try {
            // Run reg query, then read output with StreamReader (internal class)
            Process process = Runtime.getRuntime().exec("reg query " + '"'+ registryPath + "\" /v " + registryKey);

            StreamReader reader = new StreamReader(process.getInputStream());
            reader.start();
            process.waitFor();
            reader.join();
            String output = reader.getResult();

            String[] arrOutput = output.split(registryKey)[1].replace("\r", "").replace("\n", "").trim().split(" ");

            Map<RegistryKeyHeadersEnum, Object> keyData = new HashMap<>();
            keyData.put(RegistryKeyHeadersEnum.NAME, registryKey);
            keyData.put(RegistryKeyHeadersEnum.TYPE, arrOutput[0]);
            keyData.put(RegistryKeyHeadersEnum.DATA, arrOutput[arrOutput.length-1]);

            return keyData;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get a registry key value
     * @param registryPath the registry path - must be in this format: HKEY_CURRENT_USER\\Control Panel\\International
     * @param registryKey the registry key
     * @return a registry key value
     * @author genosar.dafna
     * @since 27.06.2023
     */
    public static <T> T getRegistryKeyValue(String registryPath, String registryKey) {
        Map<RegistryKeyHeadersEnum, ?> registryKeyData = getRegistryKeyData(registryPath, registryKey);
        return (T)registryKeyData.get(RegistryKeyHeadersEnum.DATA);
    }
}
