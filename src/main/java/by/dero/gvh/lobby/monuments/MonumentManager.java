package by.dero.gvh.lobby.monuments;

import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.interfaces.SingleBoostInterface;
import by.dero.gvh.lobby.interfaces.TeamBoostInterface;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.PlayerRunnable;
import lombok.Getter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.*;

public class MonumentManager implements Listener {
    private final HashMap<String, Class<? extends Monument>> classNameToMonument = new HashMap<>();
    private final HashMap<String, Monument> monuments = new HashMap<>();
    @Getter private final ArrayList<BoosterStand> boosters = new ArrayList<>();
    @Getter private final HashMap<UUID, PlayerRunnable> onClick = new HashMap<>();
    @Getter private final HashMap<UUID, HashMap<Integer, PlayerRunnable>> onShiftClick = new HashMap<>();
    
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
    
        System.err.println(Lobby.getInstance().getInfo().getTeamBooster() + " " + Lobby.getInstance().getInfo().getTeamBooster());
        boosters.add(new BoosterStand(Lobby.getInstance().getInfo().getTeamBooster(),
                Lang.get("lobby.teamBooster"), "teambooster", TeamBoostInterface.class));
    
        boosters.add(new BoosterStand(Lobby.getInstance().getInfo().getSingleBooster(),
                Lang.get("lobby.singleBooster"), "singlebooster", SingleBoostInterface.class));
    }

    public void unload() {
        for (Monument monument : monuments.values()) {
            monument.unload();
        }
        monuments.clear();
        for (BoosterStand stand : boosters) {
            stand.unload();
        }
        boosters.clear();
    }

    private void registerMonuments() {
        registerMonument("thor", ArmorStandMonument.class);
        registerMonument("warrior", ArmorStandMonument.class);
        registerMonument("lucifer", ArmorStandMonument.class);
        registerMonument("assassin", ArmorStandMonument.class);
        registerMonument("dovahkiin", ArmorStandMonument.class);
        registerMonument("horseman", ArmorStandMonument.class);
//        registerMonument("hachick", ArmorStandMonument.class);
        registerMonument("paladin", ArmorStandMonument.class);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        event.setCancelled(true);
        UUID entityId = event.getRightClicked().getUniqueId();
        Player player = event.getPlayer();
        PlayerRunnable runnable;
        HashMap<Integer, PlayerRunnable> cur = onShiftClick.getOrDefault(entityId, null);
        if (player.isSneaking() && cur != null) {
            runnable = cur.getOrDefault(player.getInventory().getHeldItemSlot(), null);
            if (runnable != null) {
                runnable.run(player);
                return;
            }
            runnable = cur.getOrDefault(-1, null);
            if (runnable != null) {
                runnable.run(player);
                return;
            }
        }
        
        runnable = onClick.getOrDefault(entityId, null);
        if (runnable != null) {
            runnable.run(player);
            return;
        }
        
        Collection<Monument> playerMonuments = Lobby.getInstance().getMonumentManager().getMonuments().values();
        for (Monument monument : playerMonuments) {
            if (!(monument instanceof ArmorStandMonument)) {
                continue;
            }
            ArmorStandMonument standMonument = (ArmorStandMonument) monument;
            ArmorStand armorStand = standMonument.getArmorStand();
            if (armorStand.getUniqueId().equals(entityId)) {
                standMonument.onSelect(player);
                Lobby.getInstance().updateDisplays(player);
            }
        }
        
        for (BoosterStand stand : boosters) {
            if (stand.getStand().getUniqueId().equals(entityId)) {
                stand.onClick(player);
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
