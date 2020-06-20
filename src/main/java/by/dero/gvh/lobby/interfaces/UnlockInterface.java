package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.LobbyPlayer;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.UnitClassDescription;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class UnlockInterface extends Interface {
    public UnlockInterface(InterfaceManager manager, Player player, String className) {
        super(manager, player, 6,
                (Lobby.getInstance().getPlayers().get(player.getName()).getPlayerInfo().canUnlock(className)
                ? Lang.get("interfaces.unlock") : Lang.get("interfaces.unlockNotEnough")));
        UnitClassDescription classDescription = Plugin.getInstance().getData().getClassNameToDescription().get(className);

        ItemStack emptySlot = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        List<String> itemNames = new LinkedList<>();
        for (String itemName : classDescription.getItemNames()) {
            if (itemNames.size() == 9) {
                break;
            }
            if (Plugin.getInstance().getData().getItems().get(itemName).getLevels().size() < 2) {
                continue;
            }
            itemNames.add(itemName);
        }
        int index = 0;
        for (; index < Math.max(0, (9 - itemNames.size()) / 2); ++index) {
            addItem(index, 0, emptySlot);
        }
        for (String itemName : itemNames) {
            addItem(index++, 0,
                    Item.getItemStack(itemName,
                            Plugin.getInstance().getData().getItems().get(itemName).getLevels().get(0)));
        }
        for (; index < 9; ++index) {
            addItem(index, 0, emptySlot);
        }
        boolean canUnlock = Lobby.getInstance().getPlayers().get(player.getName()).getPlayerInfo().canUnlock(className);
        if (canUnlock) {
            Runnable action = () -> {
                player.sendMessage(Lang.get("interfaces.unlocked").replace("%className%", Lang.get("classes." + className)).
                        replace("%cost%", String.valueOf(classDescription.getCost())));
                LobbyPlayer lobbyPlayer = Lobby.getInstance().getPlayers().get(player.getName());
                lobbyPlayer.getPlayerInfo().unlockClass(className);
                lobbyPlayer.saveInfo();
                Lobby.getInstance().updateDisplays(player);
                close();
            };
            ItemStack itemStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
            for (int x = 0; x < 9; ++x) {
                for (int y = 1; y < 6; ++y) {
                    addButton(x, y, itemStack, action);
                }
            }
        } else {
            Runnable action = () -> {
                LobbyPlayer lobbyPlayer = Lobby.getInstance().getPlayers().get(player.getName());
                player.sendMessage(Lang.get("interfaces.notUnlocked").replace("%className%", Lang.get("classes." + className)).
                        replace("%cost%", String.valueOf(classDescription.getCost())).
                        replace("%remains%", String.valueOf(classDescription.getCost() - lobbyPlayer.getPlayerInfo().getBalance())));
                close();
            };
            ItemStack itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            for (int x = 0; x < 9; ++x) {
                for (int y = 1; y < 6; ++y) {
                    addButton(x, y, itemStack, action);
                }
            }
        }
    }
}
