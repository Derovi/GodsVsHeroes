package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.*;
import by.dero.gvh.utils.BridgeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class CompassInterface extends Interface {
    private final BukkitRunnable interfaceUpdater;

    public CompassInterface(InterfaceManager manager, Player player) {
        super(manager, player, 1, Lang.get("compass.title"));
        interfaceUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                updateItems();
            }
        };
        interfaceUpdater.runTaskTimer(Plugin.getInstance(), 0, 20);
    }

    @Override
    public void onInventoryClosed() {
        interfaceUpdater.cancel();
    }

    private void updateItems() {
        clear();
        int index = 0;
        for (ServerInfo info : Plugin.getInstance().getServerData().getGameServers()) {
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
            if (!info.getStatus().equals(Game.State.WAITING.toString())) {
                addItem(index % 9, index / 9, itemStack);
            } else {
                addButton(index % 9, index / 9, itemStack, () -> BridgeUtils.redirectPlayer(getPlayer(), info.getName()));
            }
            ++index;
        }
        //update();
    }
}
