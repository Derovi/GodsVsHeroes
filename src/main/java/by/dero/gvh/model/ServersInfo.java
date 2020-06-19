package by.dero.gvh.model;

import java.util.*;

public class ServersInfo {
    private final Map<String, ServerInfo> servers = new HashMap<>();

    public void updateServerInfo(ServerInfo info) {
        servers.put(info.getName(), info);
    }

    public void register(String serverName, ServerType type) {
        servers.put(serverName, new ServerInfo(serverName, type));
    }

    public void updateStatus(String serverName, String status) {
        servers.get(serverName).setStatus(status);
    }

    public void updateOnline(String serverName, int online) {
        servers.get(serverName).setOnline(online);
    }

    public Map<String, ServerInfo> getServers() {
        return servers;
    }
}
