package common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class Configurations {
    public static String getValueByKey(String key) {
        Properties prop = new Properties();
        try {
            prop.load(new InputStreamReader(new FileInputStream("configurations.properties"), "UTF-8"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return prop.getProperty(key);
    }
}
