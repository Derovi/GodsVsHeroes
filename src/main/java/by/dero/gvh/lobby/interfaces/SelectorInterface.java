package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.minigame.Heads;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.nmcapi.NMCUtils;
import by.dero.gvh.utils.InterfaceUtils;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SelectorInterface extends Interface {
    public SelectorInterface(InterfaceManager manager, Player player, String className) {
        super(manager, player, 6, Lang.get("lobby.selectHero").replace("%class%", Lang.get("classes." + className)));
        String[] pattern =  {
                "RRRRRRRRR",
                "REEESEEER",
                "REBBEGGER",
                "REBBEGGER",
                "REEEEEEER",
                "RREEHEERR",
        };
        
        Runnable select = () -> {
            PlayerInfo playerInfo = Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName());
            playerInfo.selectClass(className);
            Plugin.getInstance().getPlayerData().savePlayerInfo(playerInfo);
            Lobby.getInstance().updateDisplays(getPlayer());
            close();
        };

        Runnable upgrade = () -> {
            close();
            UpgradeInterface upgradeInterface = new UpgradeInterface(manager, player, className);
            upgradeInterface.open();
        };
        
        Runnable onSettings = () -> {
            close();
            SlotCustomizerInterface inter = new SlotCustomizerInterface(manager, player, className);
            inter.setOnBackButton(this::open);
            inter.open();
        };

        ItemStack selectItemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
        InterfaceUtils.changeName(selectItemStack, Lang.get("interfaces.select"));
        ItemStack upgradeItemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 11);
        InterfaceUtils.changeName(upgradeItemStack, Lang.get("interfaces.upgradeSelect"));
        ItemStack returnItemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
        InterfaceUtils.changeName(returnItemStack, Lang.get("interfaces.back"));
        ItemStack skull = Heads.getHead(className);
        InterfaceUtils.changeName(skull, Lang.get("interfaces.stats"));
//        ItemStack emptySlot = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 8);
//        InterfaceUtils.changeName(emptySlot, Lang.get("interfaces.empty"));
        ItemStack settings = new ItemStack(Material.CLAY_BALL);
        InterfaceUtils.changeName(settings, Lang.get("interfaces.slotCustomizerTitle"));
        NBTTagCompound compound = NMCUtils.getNBT(settings);
        compound.set("other", new NBTTagString("settings"));
        NMCUtils.setNBT(settings, compound);
        
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 6; y++) {
                switch (pattern[y].charAt(x)) {
                    case 'R' : addButton(x, y, returnItemStack, this::close); break;
//                    case 'E' : addItem(x, y, emptySlot); break;
                    case 'G' : addButton(x, y, selectItemStack, select); break;
                    case 'B' : addButton(x, y, upgradeItemStack, upgrade); break;
                    case 'H' : addItem(x, y, skull); break;
                    case 'S' : addButton(x, y, settings, onSettings);
                }
            }
        }
    }
}
