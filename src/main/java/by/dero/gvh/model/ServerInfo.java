package by.dero.gvh.model;

public class ServerInfo {
    private String name;
    private ServerType type;
    private String status = "NO_STATUS";
    private int online;
    private int maxOnline = 24;

    public ServerInfo() {
    }

    public ServerInfo(String name, ServerType type) {
        this.name = name;
        this.type = type;
    }

    public int getMaxOnline() {
        return maxOnline;
    }

    public void setMaxOnline(int maxOnline) {
        this.maxOnline = maxOnline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServerType getType() {
        return type;
    }

    public void setType(ServerType type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }
}
