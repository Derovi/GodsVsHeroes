package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.LobbyPlayer;
import by.dero.gvh.model.*;
import by.dero.gvh.utils.InterfaceUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;

public class UpgradeInterface extends Interface {
    private final String className;
    private final InterfaceManager manager;

    public UpgradeInterface(InterfaceManager manager, Player player, String className) {
        super(manager, player, 6, className);
        this.manager = manager;
        this.className = className;
        PlayerInfo playerInfo = Lobby.getInstance().getPlayers().get(player.getName()).getPlayerInfo();
        updateAll(playerInfo);
    }

    public void updateItemLine(int position, String itemName, PlayerInfo info) {
        int currentLevel = info.getItemLevel(className, itemName);
        addItem(position, 0,
                Plugin.getInstance().getData().getItems().get(itemName).getLevels().get(currentLevel).getItemStack(getPlayer()));
        for (int index = 1; index <= currentLevel; ++index) {
            ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 3);
            InterfaceUtils.changeName(itemStack, Lang.get("interfaces.upgraded"));
            addItem(position, index, itemStack);
        }
        int maxLevel = Plugin.getInstance().getData().getItems().get(itemName).getLevels().size() - 1;
        if (currentLevel != maxLevel) {
            ItemInfo itemInfo = Plugin.getInstance().getData().getItems().get(itemName).getLevels().get(currentLevel+1);
            if (info.canUpgradeItem(className, itemName)) {
                ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
                // ADD lore
                ItemMeta meta = itemStack.getItemMeta();
                meta.setLore(itemInfo.getLore());
                itemStack.setItemMeta(meta);
                InterfaceUtils.changeName(itemStack, Lang.get("interfaces.upgrade"));
                addButton(position, currentLevel + 1, itemStack, () -> {
                    LobbyPlayer lobbyPlayer = Lobby.getInstance().getPlayers().get(getPlayer().getName());
                    lobbyPlayer.getPlayerInfo().upgradeItem(className, itemName);
                    lobbyPlayer.saveInfo();
                    updateAll(lobbyPlayer.getPlayerInfo());
                    Lobby.getInstance().updateDisplays(getPlayer());
                    update();
                });
            } else {
                ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 8);
                ItemMeta meta = itemStack.getItemMeta();
                meta.setLore(itemInfo.getLore());
                itemStack.setItemMeta(meta);
                InterfaceUtils.changeName(itemStack, Lang.get("interfaces.upgradeNE").
                        replace("%cost%", String.valueOf(itemInfo.getCost())));
                addItem(position, currentLevel + 1, itemStack);
            }
            for (int index = currentLevel + 2; index <= maxLevel; ++index) {
                ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 0);
                InterfaceUtils.changeName(itemStack, Lang.get("interfaces.notAvailable"));
                
                addItem(position, index, itemStack);
            }
        }
        for (int index = maxLevel + 1; index < 6; ++index) {
            ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
            InterfaceUtils.changeName(itemStack, Lang.get("interfaces.back"));
            addButton(position, index, itemStack, () -> {
                close();
                SelectorInterface selectorInterface = new SelectorInterface(manager, getPlayer(), className);
                selectorInterface.open();
            });
        }
    }

    public void updateAll(PlayerInfo info) {
        UnitClassDescription classDescription = Plugin.getInstance().getData().getClassNameToDescription().get(className);
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
            fillEmptyLine(index);
        }
        for (String itemName : itemNames) {
            updateItemLine(index++, itemName, info);
        }
        for (; index < 9; ++index) {
            fillEmptyLine(index);
        }
    }

    public void fillEmptyLine(int position) {
        ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
        InterfaceUtils.changeName(itemStack, Lang.get("interfaces.back"));
        for (int idx = 0; idx < 6; ++idx) {
            addButton(position, idx, itemStack, () -> {
                close();
                SelectorInterface selectorInterface = new SelectorInterface(manager, getPlayer(), className);
                selectorInterface.open();
            });
        }
    }
}
