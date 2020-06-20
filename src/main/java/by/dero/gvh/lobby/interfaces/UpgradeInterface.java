package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.LobbyPlayer;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.model.UnitClassDescription;
import by.dero.gvh.utils.InterfaceUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

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
                Item.getItemStack(itemName,
                        Plugin.getInstance().getData().getItems().get(itemName).getLevels().get(currentLevel)));
        for (int index = 1; index <= currentLevel; ++index) {
            ItemStack itemStack = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
            InterfaceUtils.changeName(itemStack, Lang.get("interfaces.upgraded"));
            addItem(position, index, itemStack);
        }
        int maxLevel = Plugin.getInstance().getData().getItems().get(itemName).getLevels().size() - 1;
        if (currentLevel != maxLevel) {
            if (info.canUpgradeItem(className, itemName)) {
                ItemStack itemStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
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
                ItemStack itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                InterfaceUtils.changeName(itemStack, Lang.get("interfaces.upgradeNE"));
                addItem(position, currentLevel + 1, itemStack);
            }
            for (int index = currentLevel + 2; index <= maxLevel; ++index) {
                ItemStack itemStack = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                InterfaceUtils.changeName(itemStack, Lang.get("interfaces.notAvailable"));
                addItem(position, index, itemStack);
            }
        }
        for (int index = maxLevel + 1; index < 6; ++index) {
            ItemStack itemStack = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
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
        ItemStack itemStack = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
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
