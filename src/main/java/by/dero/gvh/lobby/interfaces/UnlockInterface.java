package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.books.ItemDescriptionBook;
import by.dero.gvh.donate.Donate;
import by.dero.gvh.donate.DonateType;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.PlayerLobby;
import by.dero.gvh.lobby.monuments.ArmorStandMonument;
import by.dero.gvh.minigame.Heads;
import by.dero.gvh.model.CustomizationContext;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.model.UnitClassDescription;
import by.dero.gvh.utils.InterfaceUtils;
import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static by.dero.gvh.model.Drawings.spawnUnlockParticles;

public class UnlockInterface extends Interface {
    public UnlockInterface(InterfaceManager manager, Player player, String className) {
        super(manager, player, 6,
                (Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName()).canUnlock(className)
                ? Lang.get("interfaces.unlockTitle") : Lang.get("interfaces.unlockNETitle")).
                        replace("%class%", Lang.get("classes." + className)).
                        replace("%cost%", String.valueOf(Plugin.getInstance().getData().
                                getClassNameToDescription().get(className).getCost())));
        
        String[] pattern = {
                "RRRRRRRRR",
                "REEEEEEER",
                "REEGGGEER",
                "REEGGGEER",
                "REEGGGEER",
                "RREEHEERR",
        };

        UnitClassDescription classDescription = Plugin.getInstance().getData().getClassNameToDescription().get(className);

        ItemStack emptySlot = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 8);
        ItemStack returnSlot = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
        ItemStack skull = Heads.getHead(className);
        
        InterfaceUtils.changeName(returnSlot, Lang.get("interfaces.back"));
        InterfaceUtils.changeName(emptySlot, Lang.get("interfaces.empty"));
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
            addButton(index, 0, returnSlot, this::close);
        }
        for (String itemName : itemNames) {
            addButton(index++, 0, Plugin.getInstance().getData().getItems().get(itemName).getLevels().get(0)
                            .getItemStack(new CustomizationContext(player, className)),
                    () -> {
                        ItemDescriptionBook book =
                                new ItemDescriptionBook(Plugin.getInstance().getBookManager(), getPlayer(), className, itemName);
                        book.setBackAction(this::open);
                        book.build();
                        book.open();
                    });
        }
        for (; index < 9; ++index) {
            addButton(index, 0, returnSlot, this::close);
        }
        
        boolean canUnlock = Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName()).canUnlock(className);
        if (canUnlock) {
            Runnable unlockAction = () -> {
                player.sendMessage(Lang.get("interfaces.unlocked").replace("%className%", Lang.get("classes." + className)).
                        replace("%cost%", String.valueOf(classDescription.getCost())));
                PlayerInfo playerInfo = Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName());
                playerInfo.unlockClass(className);
                Plugin.getInstance().getPlayerData().savePlayerInfo(playerInfo);
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
            InterfaceUtils.changeName(itemStack, Lang.get("interfaces.unlock").
                    replace("%cost%", String.valueOf(classDescription.getCost())));
            itemStack.setLore(Lists.newArrayList(Lang.get("interfaces.costLore").
                    replace("%cost%", String.valueOf(classDescription.getCost()))));
            for (int x = 0; x < 9; x++) {
                for (int y = 1; y < 6; y++) {
                    switch (pattern[y].charAt(x)) {
                        case 'R' : addButton(x, y, returnSlot, this::close); break;
                        case 'E' : addItem(x, y, emptySlot); break;
                        case 'G' : addButton(x, y, itemStack, unlockAction); break;
                        case 'H' : addItem(x, y, skull); break;
                    }
                }
            }
//            for (int x = 0; x < 9; ++x) {
//                for (int y = 1; y < 6; ++y) {
//                    addButton(x, y, itemStack, unlockAction);
//                }
//            }
        } else {
            Runnable action = () -> {
                player.sendMessage(Lang.get("interfaces.notUnlocked").replace("%className%", Lang.get("classes." + className)).
                        replace("%cost%", String.valueOf(classDescription.getCost())).
                        replace("%remains%", String.valueOf(classDescription.getCost()
                                - Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName()).getBalance())));
                close();
            };
            ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 8);
            InterfaceUtils.changeName(itemStack, Lang.get("interfaces.unlockNE").
                    replace("%cost%", String.valueOf(classDescription.getCost())));
            for (int x = 0; x < 9; ++x) {
                for (int y = 1; y < 6; ++y) {
                    addButton(x, y, itemStack, action);
                }
            }
        }
        
        ItemStack cristItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 3);
        InterfaceUtils.changeName(cristItem, Lang.get("interfaces.unlockCrist"));
        InterfaceUtils.changeLore(cristItem, Collections.singletonList(Lang.get("interfaces.cristCostLore").
                replace("%cost%", String.valueOf(classDescription.getCost()))));
        int dx = Math.random() < 0.5 ? 1 : 6;
        int dy = Math.random() < 0.5 ? 3 : 4;
        for (int x = dx; x <= dx + 1; x++) {
            for (int y = dy; y <= dy + 1; y++) {
                addButton(x, y, cristItem, () -> {
                    ConfirmationInterface inter = new ConfirmationInterface(getManager(), getPlayer(),
                            Lang.get("interfaces.confirmBuy"), this::open, () -> {
                        Donate donate = Donate.builder()
                                .price(classDescription.getCristCost())
                                .type(DonateType.HERO)
                                .description("Buy hero " + className)
                                .onSuccessful(() -> {
                                    player.sendMessage(Lang.get("interfaces.unlockedCrist").replace("%className%", Lang.get("classes." + className)).
                                            replace("%cost%", String.valueOf(classDescription.getCristCost())));
                                    PlayerInfo playerInfo = Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName());
                                    Map<String, Integer> items = new HashMap<>();
                                    UnitClassDescription unitClassDescription = Plugin.getInstance().getData().getClassNameToDescription().get(className);
                                    for (String itemName : unitClassDescription.getItemNames()) {
                                        items.put(itemName, 0);
                                    }
                                    playerInfo.getClasses().put(className, items);
                                    Plugin.getInstance().getPlayerData().savePlayerInfo(playerInfo);
                                    
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
                                })
                                .onError(() -> {
                    
                                }).build();
                        donate.apply(getPlayer());
                    }, Lang.get("interfaces.back"), Lang.get("interfaces.confirm"), null, null);
                    inter.open();
                });
            }
        }
    }
}
