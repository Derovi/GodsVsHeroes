package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.books.ItemDescriptionBook;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.model.*;
import by.dero.gvh.utils.InterfaceUtils;
import by.dero.gvh.utils.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UpgradeInterface extends Interface {
    private final String className;
    private final InterfaceManager manager;

    public UpgradeInterface(InterfaceManager manager, Player player, String className) {
        super(manager, player, 6, Lang.get("interfaces.upgradeInterface")
                .replace("%hero%", Lang.get("classes." + className)));
        this.manager = manager;
        this.className = className;
        updateAll(Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName()));
    }

    public void updateItemLine(int position, String itemName, PlayerInfo info) {
        int currentLevel = info.getItemLevel(className, itemName);
        List<ItemInfo> infos = Plugin.getInstance().getData().getItems().get(itemName).getLevels();
        addButton(position, 0, infos.get(currentLevel)
                .getItemStack(new CustomizationContext(getPlayer(), className)), () -> {
                    ItemDescriptionBook book =
                            new ItemDescriptionBook(Plugin.getInstance().getBookManager(), getPlayer(), className, itemName);
                    book.setBackAction(this::open);
                    book.build();
                    book.open();
                });
        for (int index = 1; index <= currentLevel; ++index) {
            ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 3);
            InterfaceUtils.changeName(itemStack, Lang.get("interfaces.upgraded"));
            addItem(position, index, itemStack);
        }
        int maxLevel = infos.size() - 1;
        if (currentLevel != maxLevel) {
            ItemInfo nextInfo = infos.get(currentLevel+1);
            List<String> lore = new ArrayList<>(nextInfo.getLore());
            List<Pair<String, String>> diff = InterfaceUtils.getDifference(infos.get(currentLevel).getLore(), lore);
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, InterfaceUtils.replaceLast(lore.get(i),
                        diff.get(i).getKey(), diff.get(i).getValue()));
            }
            if (info.canUpgradeItem(className, itemName)) {
                ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
                ItemMeta meta = itemStack.getItemMeta();
                meta.setLore(lore);
                itemStack.setItemMeta(meta);
                InterfaceUtils.changeName(itemStack, Lang.get("interfaces.upgrade").
                        replace("%cost%", String.valueOf(nextInfo.getCost())));
                addButton(position, currentLevel + 1, itemStack, () -> {
                    PlayerInfo playerInfo = Plugin.getInstance().getPlayerData().getPlayerInfo(getPlayer().getName());
                    playerInfo.upgradeItem(className, itemName);
                    Plugin.getInstance().getPlayerData().savePlayerInfo(playerInfo);
                    updateAll(playerInfo);
                    Lobby.getInstance().updateDisplays(getPlayer());
                    update();
                });
            } else {
                ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7);
                ItemMeta meta = itemStack.getItemMeta();
                meta.setLore(lore);
                itemStack.setItemMeta(meta);
                InterfaceUtils.changeName(itemStack, Lang.get("interfaces.upgradeNE").
                        replace("%cost%", String.valueOf(nextInfo.getCost())));
                addItem(position, currentLevel + 1, itemStack);
            }
            for (int index = currentLevel + 2; index <= maxLevel; ++index) {
                nextInfo = infos.get(index);
                lore = new ArrayList<>(nextInfo.getLore());
                diff = InterfaceUtils.getDifference(infos.get(currentLevel).getLore(), lore);
                for (int i = 0; i < lore.size(); i++) {
                    lore.set(i, InterfaceUtils.replaceLast(lore.get(i),
                            diff.get(i).getKey(), diff.get(i).getValue()));
                }
                
                ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 0);
                ItemMeta meta = itemStack.getItemMeta();
                meta.setLore(lore);
                itemStack.setItemMeta(meta);
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
