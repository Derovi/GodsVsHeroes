package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.ServerInfo;
import by.dero.gvh.utils.BridgeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;

public class CompassInterface extends Interface {
    private final BukkitRunnable interfaceUpdater;

    public CompassInterface(InterfaceManager manager, Player player) {
        super(manager, player, calculateHeight(), Lang.get("compass.title"));
        interfaceUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                updateItems();
            }
        };
        interfaceUpdater.runTaskTimer(Plugin.getInstance(), 0, 2);
    }

    private static int calculateHeight() {
        int hei = 0, buf = 0;
        String was = "";
        for (ServerInfo info : Plugin.getInstance().getServerData().getSavedGameServers()) {
            ++buf;
            if (buf == 9 || !was.equals(info.getMode())) {
                was = info.getMode();
                ++hei;
                buf = 0;
            }
        }
        return hei;
    }

    @Override
    public void onInventoryClosed() {
        interfaceUpdater.cancel();
    }

    private void updateItems() {
        clear();
        int index = 0;
        int line = getHeight();
        String wasMode = "";
        for (ServerInfo info : Plugin.getInstance().getServerData().getSavedGameServers()) {
            ItemStack itemStack = null;
            int count = info.getOnline();
            if (count == 0) {
                count = 1;
            }
            String name = null;
            if (info.getStatus().equals(Game.State.WAITING.toString())) {
                itemStack = new ItemStack(Material.WOOL, count, (byte) 5);
                name = Lang.get("compass.waiting");
            } else if (info.getStatus().equals(Game.State.PREPARING.toString())) {
                itemStack = new ItemStack(Material.WOOL, count, (byte) 8);
                name = Lang.get("compass.preparing");
            } else if (info.getStatus().equals(Game.State.GAME_FULL.toString())) {
                itemStack = new ItemStack(Material.WOOL, count, (byte) 14);
                name = Lang.get("compass.gameFull");
            } else if (info.getStatus().equals(Game.State.FINISHING.toString())) {
                itemStack = new ItemStack(Material.WOOL, count, (byte) 4);
                name = Lang.get("compass.finishing");
            } else if (info.getStatus().equals(Game.State.GAME.toString())) {
                itemStack = new ItemStack(Material.WOOL, count, (byte) 3);
                name = Lang.get("compass.game");
            }
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(name);
            itemStack.setItemMeta(meta);
            itemStack.setLore(Collections.singletonList("§6" + Lang.get("game." + info.getMode())));
            if (!wasMode.equals(info.getMode())) {
                line--;
                index = 0;
                wasMode = info.getMode();
            }
            if (line < 0) {
                continue;
            }
            if (!info.getStatus().equals(Game.State.WAITING.toString())) {
                addItem(index, line, itemStack);
            } else {
                addButton(index, line, itemStack, () -> BridgeUtils.redirectPlayer(getPlayer(), info.getName()));
            }
            index++;
            if (index == 9) {
                index = 0;
                line--;
            }
        }
        //update();
    }
}
