package by.dero.gvh;

import java.io.*;
import java.net.URL;

public class Utils {
    public static String readResourceFile(String path) throws IOException {
        StringBuilder builder = new StringBuilder();
        try {
            InputStream is = Utils.class.getResourceAsStream(path);
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

    public static File[] getResourceFolderFiles(String folder) {
        ClassLoader loader = Utils.class.getClassLoader();
        URL url = loader.getResource(folder);
        String path = url.getPath();
        return new File(path).listFiles();
    }
}
