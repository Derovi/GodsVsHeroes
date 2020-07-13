package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.*;
import by.dero.gvh.utils.BridgeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
        System.out.println("updating");
        clear();
        int index = 0;
        for (ServerInfo info : Plugin.getInstance().getServerData().getGameServers()) {
            ItemStack itemStack = null;
            if (info.getStatus().equals(Game.State.WAITING.toString())) {
                itemStack = new ItemStack(Material.WOOL, info.getOnline(), (byte) 5);
                itemStack.getItemMeta().setDisplayName(Lang.get("compass.waiting"));
            } else if (info.getStatus().equals(Game.State.PREPARING.toString())) {
                itemStack = new ItemStack(Material.WOOL, info.getOnline(), (byte) 8);
                itemStack.getItemMeta().setDisplayName(Lang.get("compass.preparing"));
            } else if (info.getStatus().equals(Game.State.GAME_FULL.toString())) {
                itemStack = new ItemStack(Material.WOOL, info.getOnline(), (byte) 14);
                itemStack.getItemMeta().setDisplayName(Lang.get("compass.gameFull"));
            } else if (info.getStatus().equals(Game.State.FINISHING.toString())) {
                itemStack = new ItemStack(Material.WOOL, info.getOnline(), (byte) 4);
                itemStack.getItemMeta().setDisplayName(Lang.get("compass.finishing"));
            } else if (info.getStatus().equals(Game.State.GAME.toString())) {
                itemStack = new ItemStack(Material.WOOL, info.getOnline(), (byte) 3);
                itemStack.getItemMeta().setDisplayName(Lang.get("compass.game"));
            }
            if (info.getStatus().equals(Game.State.WAITING.toString())) {
                addItem(index % 9, index / 9, itemStack);
            } else {
                addButton(index % 9, index / 9, itemStack, () -> {
                    BridgeUtils.redirectPlayer(getPlayer(), info.getName());
                });
            }
            ++index;
        }
        update();
    }
}
