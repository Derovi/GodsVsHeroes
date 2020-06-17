package by.dero.gvh.lobby.monuments;

import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.monuments.ArmorStandMonument;
import by.dero.gvh.lobby.monuments.Monument;
import com.sk89q.worldedit.util.gson.GsonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class MonumentManager implements Listener {
    private final HashMap<String, Class<? extends Monument>> classNameToMonument = new HashMap<>();

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
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        UUID entityId = event.getRightClicked().getUniqueId();
        System.out.println("Interact with " + entityId.toString());
        Collection<Monument> playerMonuments = Lobby.getInstance().getActiveLobbies().get(event.getPlayer().getName()).getMonuments().values();
        for (Monument monument : playerMonuments) {
            System.out.println("Monument " + monument.getClassName());
            if (!(monument instanceof ArmorStandMonument)) {
                continue;
            }
            System.out.println("Is armor stand");
            ArmorStandMonument standMonument = (ArmorStandMonument) monument;
            ArmorStand armorStand = standMonument.getArmorStand();
            System.out.println("Interact id " + entityId);
            System.out.println("Stand id " + armorStand.getUniqueId());
            if (armorStand.getUniqueId().equals(entityId)) {
                System.out.println("id ueqials");
                standMonument.onSelect(event.getPlayer());
            }
        }
    }
}
