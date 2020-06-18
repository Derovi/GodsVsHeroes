package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.model.UnitClassDescription;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class UpgradeInterface extends Interface {
    private final String className;

    public UpgradeInterface(InterfaceManager manager, Player player, String className) {
        super(manager, player, 6, className);
        this.className = className;
        PlayerInfo playerInfo = Lobby.getInstance().getPlayers().get(player.getName()).getPlayerInfo();
        UnitClassDescription classDescription = Plugin.getInstance().getData().getClassNameToDescription().get(className);
        int index = 0;
        for (; index < Math.max(0, (9 - classDescription.getItemNames().size()) / 2); ++index) {
            fillEmptyLine(index);
        }
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
        for (String itemName : itemNames) {
            updateItemLine(index++, itemName, playerInfo);
        }
        for (; index < 9; ++index) {
            fillEmptyLine(index);
        }
    }

    public void updateItemLine(int position, String itemName, PlayerInfo info) {
        int currentLevel = info.getItemLevel(className, itemName);
        addItem(position, 0,
                Item.getItemStack(itemName,
                        Plugin.getInstance().getData().getItems().get(itemName).getLevels().get(currentLevel)));
        for (int index = 1; index <= currentLevel; ++index) {
            ItemStack itemStack = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
            addItem(position, index, itemStack);
        }
        if (currentLevel == 5) {
            return;
        }
        ItemStack currentLevelItemStack;
        if (info.canUpgradeItem(className, itemName)) {
            currentLevelItemStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        } else {
            currentLevelItemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        }
        addItem(position, currentLevel + 1, currentLevelItemStack);
        for (int index = currentLevel + 2; index < 6; ++index) {
            ItemStack itemStack = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            addItem(position, index, itemStack);
        }
    }

    public void fillEmptyLine(int position) {
        ItemStack itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for (int idx = 0; idx < 6; ++idx) {
            addItem(position, idx, itemStack);
        }
    }
}
