package by.dero.gvh.lobby.interfaces;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class InterfaceManager implements Listener {
    @Getter private final HashMap<UUID, ArrayList<Integer>> unlockedSlots = new HashMap<>();
    
    @Getter private final HashMap<String, Interface> playerNameToInterface = new HashMap<>();

    public void register(String playerName, Interface playerInterface) {
        playerNameToInterface.put(playerName, playerInterface);
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        String playerName = event.getPlayer().getName();
        if (isInterfaceOpened(playerName)) {
            playerNameToInterface.get(playerName).onInventoryClosed();
            playerNameToInterface.remove(playerName);
        }
    }
    
    @EventHandler
    public void onItemSelected(InventoryClickEvent event) {
        String playerName = event.getWhoClicked().getName();
        if (!isInterfaceOpened(playerName) || !(event.getWhoClicked() instanceof Player)) {
            event.setCancelled(true);
            return;
        }
        if (event.getClickedInventory() == event.getView().getBottomInventory()) {
            event.setCancelled(true);
            return;
        }
        ArrayList<Integer> unlocked = unlockedSlots.getOrDefault(event.getWhoClicked().getUniqueId(), null);
        if (unlocked == null || !unlocked.contains(event.getSlot())) {
            event.setCancelled(true);
        }
        playerNameToInterface.get(playerName).onSlotClicked(event);
        playerNameToInterface.get(playerName).clicked(event.getSlot());
    }

    private boolean isInterfaceOpened(String playerName) {
        return playerNameToInterface.containsKey(playerName);
    }
}
