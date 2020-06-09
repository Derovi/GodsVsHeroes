package by.dero.gvh.model;

import java.io.*;

public class LocalStorage implements StorageInterface {
    public static String getPrefix() {
        try {
            String path = LocalStorage.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI().getPath();
            path = path.replace('\\','/');
            path = path.substring(0, path.lastIndexOf('/'));
            return path + "/GodsVsHeroes/";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @Override
    public void save(String collection, String name, String object) throws IOException {
        File directory = new File(getPrefix() + collection);
        directory.mkdirs();
        BufferedWriter writer = new BufferedWriter(new FileWriter(getPrefix() + collection + "/" + name + ".json"));
        writer.write(object);
        writer.close();
    }

    @Override
    public String load(String collection, String name) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(getPrefix() + collection + "/" + name + ".json"));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            br.close();
            return sb.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @Override
    public boolean exists(String collection, String name) {
        return new File(getPrefix() + collection + "/" + name + ".json").exists();
    }
}
