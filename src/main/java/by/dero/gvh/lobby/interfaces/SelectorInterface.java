package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.LobbyPlayer;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.InterfaceUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

public class SelectorInterface extends Interface {
    public SelectorInterface(InterfaceManager manager, Player player, String className) {
        super(manager, player, 6, Lang.get("lobby.selectHero").replace("%className%", Lang.get("classes." + className)));
        Runnable select = () -> {
            LobbyPlayer lobbyPlayer = Lobby.getInstance().getPlayers().get(player.getName());
            lobbyPlayer.getPlayerInfo().selectClass(className);
            lobbyPlayer.saveInfo();
            Lobby.getInstance().updateDisplays(getPlayer());
            close();
        };

        Runnable upgrade = () -> {
            close();
            UpgradeInterface upgradeInterface = new UpgradeInterface(manager, player, className);
            upgradeInterface.open();
        };

        ItemStack selectItemStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        InterfaceUtils.changeName(selectItemStack, Lang.get("interfaces.select"));
        ItemStack upgradeItemStack = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        InterfaceUtils.changeName(upgradeItemStack, Lang.get("interfaces.upgradeSelect"));

        for (int x = 0; x < 4; ++ x) {
            for (int y = 0; y < 6; ++ y) {
                addButton(x, y, upgradeItemStack, upgrade);
            }
        }

        for (int x = 4; x < 9; ++ x) {
            for (int y = 0; y < 6; ++ y) {
                addButton(x, y, selectItemStack, select);
            }
        }
    }
}
