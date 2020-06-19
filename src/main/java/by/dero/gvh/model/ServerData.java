package by.dero.gvh.model;

import by.dero.gvh.minigame.Game;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ServerData {
    private final StorageInterface storage;

    public ServerData(StorageInterface storage) {
        this.storage = storage;
    }

    public void load() {
        try {
            if (!storage.exists("servers", "info")) {
                storage.save("servers", "info", new GsonBuilder().setPrettyPrinting().create().toJson(
                        new ServersInfo()
                ));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    public List<ServerInfo> getGameServers() {
        List<ServerInfo> servers = new LinkedList<>();
        for (ServerInfo info : getServersInfo().getServers().values()) {
            if (info.getStatus().equals(Game.State.WAITING.toString())) {
                servers.add(info);
            }
        }
        servers.sort((info1, info2) -> {
            int status1 = 0;
            if (info1.getStatus().equals(Game.State.PREPARING.toString())) {
                status1 = 1;
            } else
            if (info1.getStatus().equals(Game.State.GAME.toString())) {
                status1 = 2;
            }
            int status2 = 0;
            if (info2.getStatus().equals(Game.State.PREPARING.toString())) {
                status2 = 1;
            } else
            if (info2.getStatus().equals(Game.State.GAME.toString())) {
                status2 = 2;
            }
            if (status1 == status2) {
                return info2.getOnline() - info1.getOnline();
            }
            return status1 - status2;
        });
        return servers;
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