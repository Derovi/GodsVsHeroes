package by.dero.gvh;

import java.io.InputStream;

public class Utils {
    public static String getResourceFileText(String fileName) {
        try {
            InputStream inputStream = Utils.class
                    .getClassLoader().getResourceAsStream(fileName);
            return new String(inputStream.readAllBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
}
