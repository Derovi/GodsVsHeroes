package by.dero.gvh.model;

import by.dero.gvh.Plugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

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
        Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Bukkit.getServer().broadcastMessage("§a§l" + name + " " + this.mode), 200);
    }
}
