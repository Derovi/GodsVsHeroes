package by.dero.gvh.model;

public class ServerInfo {
    private String name;
    private ServerType type;
    private String status = "NO_STATUS";
    private int online;

    public ServerInfo() {
    }

    public ServerInfo(String name, ServerType type) {
        this.name = name;
        this.type = type;
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
