package by.dero.gvh.lobby.monuments;

import by.dero.gvh.lobby.Lobby;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class MonumentManager implements Listener {
    private final HashMap<String, Class<? extends Monument>> classNameToMonument = new HashMap<>();

    private final HashMap<UUID, UUID> active = new HashMap<>();

    public MonumentManager() {
        registerMonuments();
    }

    private void registerMonuments() {
//        registerMonument("alchemist", ArmorStandMonument.class);
//        registerMonument("archer", ArmorStandMonument.class);
//        registerMonument("default", ArmorStandMonument.class);
//        registerMonument("eyre", ArmorStandMonument.class);
//        registerMonument("heimdall", ArmorStandMonument.class);
//        registerMonument("loki", ArmorStandMonument.class);
//        registerMonument("mercenary", ArmorStandMonument.class);
        registerMonument("odin", ArmorStandMonument.class);
        registerMonument("paladin", ArmorStandMonument.class);
        registerMonument("scout", ArmorStandMonument.class);
        registerMonument("ull", ArmorStandMonument.class);

        registerMonument("warrior", ArmorStandMonument.class);
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        event.setCancelled(true);
        UUID entityId = event.getRightClicked().getUniqueId();
        Player player = event.getPlayer();
        Collection<Monument> playerMonuments = Lobby.getInstance().getActiveLobbies().get(player.getName()).getMonuments().values();
        for (Monument monument : playerMonuments) {
            if (!(monument instanceof ArmorStandMonument)) {
                continue;
            }
            ArmorStandMonument standMonument = (ArmorStandMonument) monument;
            ArmorStand armorStand = standMonument.getArmorStand();
            if (armorStand.getUniqueId().equals(entityId)) {
                standMonument.onSelect(player);
                active.put(player.getUniqueId(), entityId);
                Lobby.getInstance().updateDisplays(player);
            }
        }
    }

    public HashMap<UUID, UUID> getActive() {
        return active;
    }

    private void registerMonument(String className, Class<? extends Monument> monument) {
        classNameToMonument.put(className, monument);
    }

    public HashMap<String, Class<? extends Monument>> getClassNameToMonument() {
        return classNameToMonument;
    }
}
