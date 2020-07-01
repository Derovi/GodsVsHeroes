package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.WebThrowInfo;
import by.dero.gvh.nmcapi.SmartFallingBlock;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class WebThrow extends Item implements PlayerInteractInterface {
    private final float force;
    private final int duration;
    private final int level;

    public WebThrow(String name, int level, Player owner) {
        super(name, level, owner);
        WebThrowInfo info = ((WebThrowInfo) getInfo());
        force = info.getForce();
        duration = info.getDuration();
        this.level = info.getLevel();
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
            smartFallingBlock.setStopped(true);
            smartFallingBlock.dieLater(100);
            smartFallingBlock.setNoGravity(true);
            smartFallingBlock.setVelocity(new Vector(0,0,0));
        });
        smartFallingBlock.setOnHitEntity((Entity entity) -> {
            if (!(entity instanceof LivingEntity)) {
                return;
            }
            //smartFallingBlock.setHoldEntity(entity);
            smartFallingBlock.setNoGravity(true);
            smartFallingBlock.setVelocity(new Vector(0,0,0));
            ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, level), true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    smartFallingBlock.die();
                }
            }.runTaskLater(Plugin.getInstance(), duration);
        });
        int playerTeam = Minigame.getInstance().getGame().getPlayers().get(player.getName()).getTeam();
        smartFallingBlock.setOnEnter((Entity entity) -> {
            if (entity instanceof LivingEntity && GameUtils.isEnemy(entity, playerTeam)) {
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, level), true);
            }
        });
    }
}
