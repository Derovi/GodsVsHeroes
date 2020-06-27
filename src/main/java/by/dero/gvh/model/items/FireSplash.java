package by.dero.gvh.model.items;

import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.FireSplashInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.UUID;

import static by.dero.gvh.model.Drawings.drawSector;
import static by.dero.gvh.utils.DataUtils.damage;
import static by.dero.gvh.utils.DataUtils.isEnemy;
import static java.lang.Math.sqrt;

public class FireSplash extends Item implements PlayerInteractInterface {
    private final double radius;
    private final double damage;

    public FireSplash(final String name, final int level, final Player owner) {
        super(name, level, owner);
        final FireSplashInfo info = (FireSplashInfo) getInfo();
        radius = info.getRadius();
        damage = info.getDamage();
    }

    final HashSet<UUID> stroke = new HashSet<>();
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        final Location[] locs = drawSector(player.getEyeLocation(), 0,
                radius, Math.PI / 2, Particle.FLAME);
        stroke.clear();
        for (final Player otherPlayer : Bukkit.getOnlinePlayers()) {
            final Location other = otherPlayer.getLocation();
            for (final Location at : locs) {
                if (stroke.contains(otherPlayer.getUniqueId()) || !isEnemy(otherPlayer, team)) {
                    break;
                }
                if (other.getY() <= at.getY() && at.getY() <= other.getY() + 2) {
                    final double dst = sqrt((other.getX() - at.getX()) * (other.getX() - at.getX()) +
                                        (other.getZ() - at.getZ()) * (other.getZ() - at.getZ()));
                    if (dst < 1) {
                        damage(damage, otherPlayer, owner);
                        stroke.add(otherPlayer.getUniqueId());
                    }
                }
            }
        }
    }
}
