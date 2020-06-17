package by.dero.gvh.lobby.monuments;

import by.dero.gvh.lobby.monuments.ArmorStandMonument;
import by.dero.gvh.lobby.monuments.Monument;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.HashMap;
import java.util.UUID;

public class MonumentManager implements Listener {
    private HashMap<String, Class<? extends Monument>> classNameToMonument;

    public MonumentManager() {
        registerMonuments();
    }

    private void registerMonuments() {
        registerMonument("default", ArmorStandMonument.class);
    }

    private void registerMonument(String className, Class<? extends Monument> monument) {
        classNameToMonument.put(className, monument);
    }

    public HashMap<String, Class<? extends Monument>> getClassNameToMonument() {
        return classNameToMonument;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        UUID entityId = event.getRightClicked().getUniqueId();
    }
}
