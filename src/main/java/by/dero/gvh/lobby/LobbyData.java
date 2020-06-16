package by.dero.gvh.lobby;

import by.dero.gvh.model.StorageInterface;

public class LobbyData {
    private StorageInterface storage;

    public LobbyData(StorageInterface storage) {
        this.storage = storage;
    }

    public StorageInterface getStorage() {
        return storage;
    }
}
