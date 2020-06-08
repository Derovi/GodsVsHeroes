package by.dero.gvh.model;

import java.io.FileNotFoundException;

public interface StorageInterface {
    void save(String collection, String name, String object) throws FileNotFoundException;
    String load(String collection, String name);
    boolean exists(String collection, String name);
}
