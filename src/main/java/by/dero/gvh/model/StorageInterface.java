package by.dero.gvh.model;

import java.io.IOException;

public interface StorageInterface {
    void save(String collection, String name, String object) throws IOException;
    String load(String collection, String name);
    boolean exists(String collection, String name);
}
