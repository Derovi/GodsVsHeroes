package by.dero.gvh.lobby.monuments;

import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.utils.DirectedPosition;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.*;

public class MonumentManager implements Listener {
    private final HashMap<String, Class<? extends Monument>> classNameToMonument = new HashMap<>();
    private final HashMap<String, Monument> monuments = new HashMap<>();

    public MonumentManager() {
        registerMonuments();
    }

    public void load() {
        for (Map.Entry<String, DirectedPosition> entry :
                Lobby.getInstance().getInfo().getClassNameToMonumentPosition().entrySet()) {
            try {
                String monumentName = entry.getKey();
                Monument monument = getClassNameToMonument().
                        get(monumentName).getConstructor(DirectedPosition.class, String.class).
                        newInstance(entry.getValue(), monumentName);
                monument.load();
                monuments.put(monumentName, monument);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void unload() {
        for (Monument monument : monuments.values()) {
            monument.unload();
        }
        monuments.clear();
    }

    private void registerMonuments() {
        registerMonument("thor", ArmorStandMonument.class);
        registerMonument("warrior", ArmorStandMonument.class);
        registerMonument("lucifer", ArmorStandMonument.class);
        registerMonument("assassin", ArmorStandMonument.class);
        registerMonument("dovahkiin", ArmorStandMonument.class);
        registerMonument("horseman", ArmorStandMonument.class);
        registerMonument("hachick", ArmorStandMonument.class);
        registerMonument("paladin", ArmorStandMonument.class);
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        System.out.println("Interact!");
        event.setCancelled(true);
        UUID entityId = event.getRightClicked().getUniqueId();
        Player player = event.getPlayer();
        Collection<Monument> playerMonuments = Lobby.getInstance().getMonumentManager().getMonuments().values();
        for (Monument monument : playerMonuments) {
            System.out.println("mon: " + monument.getClassName());
            if (!(monument instanceof ArmorStandMonument)) {
                continue;
            }
            ArmorStandMonument standMonument = (ArmorStandMonument) monument;
            ArmorStand armorStand = standMonument.getArmorStand();
            if (armorStand.getUniqueId().equals(entityId)) {
                System.out.println("equals");
                standMonument.onSelect(player);
                Lobby.getInstance().updateDisplays(player);
            }
        }
    }

    public HashMap<String, Monument> getMonuments() {
        return monuments;
    }

    private void registerMonument(String className, Class<? extends Monument> monument) {
        classNameToMonument.put(className, monument);
    }

    public HashMap<String, Class<? extends Monument>> getClassNameToMonument() {
        return classNameToMonument;
    }
}
