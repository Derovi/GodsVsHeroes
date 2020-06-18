package by.dero.gvh.lobby.interfaces;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;

public class InterfaceManager implements Listener {
    private final HashMap<String, Interface> playerNameToInterface = new HashMap<>();

    public void register(String playerName, Interface playerInterface) {
        playerNameToInterface.put(playerName, playerInterface);
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        String playerName = event.getPlayer().getName();
        if (isInterfaceOpened(playerName)) {
            playerNameToInterface.remove(playerName);
        }
    }

    @EventHandler
    public void onItemSelected(InventoryClickEvent event) {
        String playerName = event.getWhoClicked().getName();
        if (!isInterfaceOpened(playerName)) {
            return;
        }
        event.setCancelled(true);
        playerNameToInterface.get(playerName).clicked(event.getSlot());
    }

    private boolean isInterfaceOpened(String playerName) {
        return playerNameToInterface.containsKey(playerName);
    }

    public HashMap<String, Interface> getPlayerNameToInterface() {
        return playerNameToInterface;
    }
}
