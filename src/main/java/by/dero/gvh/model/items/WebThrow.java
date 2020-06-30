package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.WebThrowInfo;
import by.dero.gvh.nmcapi.SmartFallingBlock;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class WebThrow extends Item implements PlayerInteractInterface {
    private final float force;
    private final int duration;
    private final float multiplier;

    public WebThrow(String name, int level, Player owner) {
        super(name, level, owner);
        WebThrowInfo info = ((WebThrowInfo) getInfo());
        force = info.getForce();
        duration = info.getDuration();
        multiplier = info.getMultiplier();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        Player player = event.getPlayer();
        SmartFallingBlock smartFallingBlock = new SmartFallingBlock(player.getLocation().add(0,1,0), Material.WEB);
        smartFallingBlock.setVelocity(player.getLocation().getDirection().multiply(force));
        smartFallingBlock.spawn();
        smartFallingBlock.setOwner(player);
        smartFallingBlock.setOnHitGround(() -> {
            System.out.println("Hit ground");
            smartFallingBlock.setStopped(true);
            smartFallingBlock.dieLater(100);
            smartFallingBlock.setNoGravity(true);
            smartFallingBlock.setVelocity(new Vector(0,0,0));
        });
        smartFallingBlock.setOnHitEntity((Entity entity) -> {
            System.out.println("Hit entity");
            if (!(entity instanceof Player)) {
                return;
            }
            System.out.println("Hit player");
            smartFallingBlock.setHoldEntity(entity);
            smartFallingBlock.setNoGravity(true);
            smartFallingBlock.setVelocity(new Vector(0,0,0));
            Player target = (Player) entity;
            float speed = target.getWalkSpeed();
            target.setWalkSpeed(speed * multiplier);
            new BukkitRunnable() {
                @Override
                public void run() {
                    target.setWalkSpeed(speed);
                    smartFallingBlock.die();
                }
            }.runTaskLater(Plugin.getInstance(), duration);
        });
    }
}
