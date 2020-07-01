package by.dero.gvh.model.items;

import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.NinjaRopeInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class NinjaRope extends Item implements PlayerInteractInterface {
    private final int range;
    private final double forceMultiplier;

    public NinjaRope(String name, int level, Player owner) {
        super(name, level, owner);
        NinjaRopeInfo info = (NinjaRopeInfo) getInfo();
        range = info.getRange();
        forceMultiplier = info.getForceMultiplier();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        LivingEntity livTarget = GameUtils.getTargetEntity(owner, range);
        if (livTarget != null) {
            cooldown.reload();
            Drawings.drawLine(livTarget.getEyeLocation(), owner.getEyeLocation(), Particle.FLAME);
            livTarget.setVelocity(
                    owner.getEyeLocation().subtract(livTarget.getEyeLocation()).toVector().multiply(forceMultiplier*2)
            );
            return;
        }

        BlockIterator it = new BlockIterator(owner.getEyeLocation(), 0, range);
        while (it.hasNext()) {
            Block block = it.next();
            if (block.getType().equals(Material.AIR)) {
                continue;
            }
            Drawings.drawLine(block.getLocation(), owner.getEyeLocation(), Particle.FLAME);
            double dst = block.getLocation().distance(owner.getEyeLocation());
            Vector vel = block.getLocation().subtract(owner.getEyeLocation()).toVector().
                    add(new Vector(0, 0.1 * dst, 0)).normalize().multiply(dst * forceMultiplier);
            owner.setVelocity(vel);
            cooldown.reload();
            return;
        }
    }
}
