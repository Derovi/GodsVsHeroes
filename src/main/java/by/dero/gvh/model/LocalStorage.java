package by.dero.gvh.model;

import java.io.*;

public class LocalStorage implements StorageInterface {
    @Override
    public void save(String collection, String name, String object) {
        try {
            PrintWriter printWriter = new PrintWriter(collection + "/" + name + ".json");
            printWriter.println(object);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String load(String collection, String name) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(collection + "/" + name + ".json"));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @Override
    public boolean exists(String collection, String name) {
        return new File(collection + "/" + name + ".json").exists();
    }
}
