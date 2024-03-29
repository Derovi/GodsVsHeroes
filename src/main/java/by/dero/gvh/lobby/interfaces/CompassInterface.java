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
        int count = Plugin.getInstance().getServerData().getSavedGameServers().size();
        return (count / 9) + ((count % 9 != 0) ? 1 : 0);
    }

    @Override
    public void onInventoryClosed() {
        interfaceUpdater.cancel();
    }

    private void updateItems() {
        clear();
        int index = 0;
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
            if (!info.getStatus().equals(Game.State.WAITING.toString())) {
                addItem(index % 9, getHeight() - 1 - index / 9, itemStack);
            } else {
                if (getHeight() - 1 - index / 9 < getHeight()) {
                    addButton(index % 9, getHeight() - 1 - index / 9, itemStack, () -> BridgeUtils.redirectPlayer(getPlayer(), info.getName()));
                }
            }
            ++index;
        }
        //update();
    }
}
