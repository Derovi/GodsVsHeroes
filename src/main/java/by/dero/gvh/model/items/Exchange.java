package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.ExchangeInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Exchange extends Item implements PlayerInteractInterface {
    private final double maxRange;
    private final Material material;
    public Exchange(String name, int level, Player owner) {
        super(name, level, owner);
        ExchangeInfo info = (ExchangeInfo) getInfo();
        maxRange = info.getMaxRange();
        material = info.getMaterial();
    }

    final int parts = 300;
    public void drawSign(final LivingEntity player) {
        double radius = 0.7;
        for (int ticks = 0; ticks < 5; ticks++) {
            Drawings.drawCircleInFront(player.getEyeLocation(), radius, 3, Particle.PORTAL);
            radius += 0.3;
        }

        Drawings.drawCircleInFront(player.getEyeLocation(), radius, 3, Particle.PORTAL);
        for (int i = 0; i < parts; i++) {
            player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE,
                    MathUtils.randomCylinder(player.getLocation(), 1.3, -2), 0);
        }
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final LivingEntity target = GameUtils.getTargetEntity(owner, maxRange,
                (e) -> e.getVehicle() == null && e.getPassengers().isEmpty() && GameUtils.isEnemy(e, getTeam()));

        if (target != null) {
            if (!cooldown.isReady()) {
                return;
            }
            cooldown.reload();
            owner.setCooldown(material, (int) cooldown.getDuration());
            Location oLoc = owner.getLocation(), tLoc = target.getLocation();
            drawSign(owner);
            drawSign(target);
            owner.teleport(new Location(tLoc.world, tLoc.x, tLoc.y, tLoc.z, oLoc.yaw, oLoc.pitch));
            target.teleport(new Location(oLoc.world, oLoc.x, oLoc.y, oLoc.z, tLoc.yaw, tLoc.pitch));
            owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_ILLUSION_ILLAGER_MIRROR_MOVE, 1.07f, 1);
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ILLUSION_ILLAGER_MIRROR_MOVE, 1.07f, 1);
        }
    }
}
