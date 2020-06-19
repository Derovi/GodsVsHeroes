package by.dero.gvh.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ServerData {
    private final StorageInterface storage;

    public ServerData(StorageInterface storage) {
        this.storage = storage;
    }

    public ServersInfo getServersInfo() {
        return new Gson().fromJson(storage.load("servers", "info"), ServersInfo.class);
    }

    public void saveServersInfo(ServersInfo info) {
        try {
            storage.save("servers", "info", new GsonBuilder().setPrettyPrinting().create().toJson(info));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ServerInfo getServerInfo(String serverName) {
        return getServersInfo().get(serverName);
    }

    public void updateServerInfo(ServerInfo info) {
        ServersInfo globalInfo = getServersInfo();
        globalInfo.updateServerInfo(info);
        saveServersInfo(globalInfo);
    }

    public void register(String serverName, ServerType type) {
        ServersInfo globalInfo = getServersInfo();
        globalInfo.register(serverName, type);
        saveServersInfo(globalInfo);
    }

    public void updateStatus(String serverName, String status) {
        ServersInfo globalInfo = getServersInfo();
        globalInfo.updateStatus(serverName, status);
        saveServersInfo(globalInfo);
    }

    public void updateOnline(String serverName, int online) {
        ServersInfo globalInfo = getServersInfo();
        globalInfo.updateOnline(serverName, online);
        saveServersInfo(globalInfo);
    }
}
