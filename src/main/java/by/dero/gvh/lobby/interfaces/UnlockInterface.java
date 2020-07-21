package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.LobbyPlayer;
import by.dero.gvh.lobby.PlayerLobby;
import by.dero.gvh.lobby.monuments.ArmorStandMonument;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.UnitClassDescription;
import by.dero.gvh.utils.InterfaceUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;

import static by.dero.gvh.model.Drawings.spawnUnlockParticles;

public class UnlockInterface extends Interface {
    public UnlockInterface(InterfaceManager manager, Player player, String className) {
        super(manager, player, 6,
                (Lobby.getInstance().getPlayers().get(player.getName()).getPlayerInfo().canUnlock(className)
                ? Lang.get("interfaces.unlockTitle") : Lang.get("interfaces.unlockNETitle")));
        UnitClassDescription classDescription = Plugin.getInstance().getData().getClassNameToDescription().get(className);

        ItemStack emptySlot = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 0);
        InterfaceUtils.changeName(emptySlot, Lang.get("interfaces.empty"));
        List<String> itemNames = new LinkedList<>();
        for (String itemName : classDescription.getItemNames()) {
            if (itemNames.size() == 9) {
                break;
            }
            System.out.println("itemName:" + itemName);
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
                            Plugin.getInstance().getData().getItems().get(itemName).getLevels().get(0).getItemStack(player));
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
                final PlayerLobby lobby = Lobby.getInstance().getActiveLobbies().get(player.getName());
                final ArmorStand stand = ((ArmorStandMonument) Lobby.getInstance().getMonumentManager().
                        getMonuments().get(className)).getArmorStand();
                final BukkitRunnable zxc = new BukkitRunnable() {
                    @Override
                    public void run() {
                        // TODO hide title
                    }
                };
                zxc.runTaskLater(Plugin.getInstance(), 240);
                lobby.getRunnables().add(zxc);

                final Location loc = stand.getLocation().clone();
                spawnUnlockParticles(loc, player, 240,
                        1.7, Math.toRadians(-70), Math.toRadians(70));

                Lobby.getInstance().updateDisplays(player);
                close();
            };
            ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
            System.out.println("Lang: " + Lang.get("interfaces.unlock"));
            InterfaceUtils.changeName(itemStack, Lang.get("interfaces.unlock"));
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
            ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 8);
            InterfaceUtils.changeName(itemStack, Lang.get("interfaces.unlockNE"));
            for (int x = 0; x < 9; ++x) {
                for (int y = 1; y < 6; ++y) {
                    addButton(x, y, itemStack, action);
                }
            }
        }
    }
}
