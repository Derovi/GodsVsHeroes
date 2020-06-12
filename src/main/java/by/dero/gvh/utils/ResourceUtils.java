package by.dero.gvh.utils;

import java.io.*;
import java.net.URL;

public class ResourceUtils {
    public static String readResourceFile(String path) throws IOException {
        StringBuilder builder = new StringBuilder();
        try {
            InputStream is = ResourceUtils.class.getResourceAsStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
            return builder.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
}
