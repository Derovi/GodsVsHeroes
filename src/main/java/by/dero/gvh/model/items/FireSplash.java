package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.FireSplashInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.UUID;

public class FireSplash extends Item implements PlayerInteractInterface, InfiniteReplenishInterface {
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
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_DEATH, 1.07f, 1);
        final Location[] locs = Drawings.drawSector(player.getEyeLocation(), 0,
                radius, Math.PI / 2, Particle.FLAME);
        stroke.clear();
        for (final Player otherPlayer : Bukkit.getOnlinePlayers()) {
            final Location other = otherPlayer.getLocation();
            for (final Location at : locs) {
                if (stroke.contains(otherPlayer.getUniqueId()) || !GameUtils.isEnemy(otherPlayer, getTeam())) {
                    break;
                }
                if (other.getY() <= at.getY() && at.getY() <= other.getY() + 2) {
                    final double dst = Math.sqrt((other.getX() - at.getX()) * (other.getX() - at.getX()) +
                                        (other.getZ() - at.getZ()) * (other.getZ() - at.getZ()));
                    if (dst < 1) {
                        GameUtils.damage(damage, otherPlayer, owner);
                        stroke.add(otherPlayer.getUniqueId());
                    }
                }
            }
        }
    }
}
