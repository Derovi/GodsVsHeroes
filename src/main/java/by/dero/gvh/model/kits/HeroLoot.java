package by.dero.gvh.model.kits;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.PlayerLobby;
import by.dero.gvh.lobby.monuments.ArmorStandMonument;
import by.dero.gvh.minigame.Heads;
import by.dero.gvh.model.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import static by.dero.gvh.model.Drawings.spawnUnlockParticles;

public class HeroLoot extends LootBoxItem {
    private final String className;

    public HeroLoot(String playerName, int chance, String className) {
        super(playerName, chance);
        this.className = className;
    }

    @Override
    public void give() {
        PlayerInfo playerInfo = Plugin.getInstance().getPlayerData().getPlayerInfo(getPlayerName());
        playerInfo.unlockClass(className);
        Plugin.getInstance().getPlayerData().savePlayerInfo(playerInfo);

        Player player = Bukkit.getPlayer(getPlayerName());
        if (player != null) {
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
        }
    }

    @Override
    public ItemStack getItemStack() {
        return Heads.getHead(className);
    }
}
