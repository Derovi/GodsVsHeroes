package by.dero.gvh.model;

import lombok.Getter;
import lombok.Setter;

public class ServerInfo {
    @Getter @Setter
    private String name;
    @Getter @Setter
    private ServerType type;
    @Getter @Setter
    private String status = "NO_STATUS";
    @Getter
    private String mode;
    @Getter @Setter
    private int online;
    @Getter @Setter
    private int maxOnline = 24;
    
    public ServerInfo(String name, ServerType type, String mode) {
        this.name = name;
        this.type = type;
        this.mode = mode;
    }
}
