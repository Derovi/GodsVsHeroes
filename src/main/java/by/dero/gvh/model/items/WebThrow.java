package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.WebThrowInfo;
import by.dero.gvh.nmcapi.SmartFallingBlock;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class WebThrow extends Item implements PlayerInteractInterface {
    private final float force;
    private final int duration;
    private final Material material;

    public WebThrow(String name, int level, Player owner) {
        super(name, level, owner);
        WebThrowInfo info = ((WebThrowInfo) getInfo());
        force = info.getForce();
        duration = info.getDuration();
        material = info.getMaterial();
    }
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        owner.setCooldown(material, (int) cooldown.getDuration());
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_EGG_THROW, 1.07f, 1);
        SmartFallingBlock smartFallingBlock = new SmartFallingBlock(owner.getLocation().add(0,1,0), Material.WEB, ownerGP.getTeam());
        smartFallingBlock.setVelocity(owner.getLocation().getDirection().multiply(force));
        smartFallingBlock.spawn();
        smartFallingBlock.setOwner(owner);
        smartFallingBlock.setOnHitGround(() -> {
            SmartFallingBlock[] blocks = new SmartFallingBlock[9];
            blocks[4] = smartFallingBlock;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    SmartFallingBlock block = smartFallingBlock;
                    if (i != 1 || j != 1) {
                        blocks[i * 3 + j] = new SmartFallingBlock(new Location(
                                smartFallingBlock.getWorld().getWorld(),
                                smartFallingBlock.locX + i - 1,
                                smartFallingBlock.locY,
                                smartFallingBlock.locZ + j - 1
                        ), Material.WEB, ownerGP.getTeam());
                        block = blocks[i * 3 + j];
                        block.setOnEnter((Entity entity) -> {
                            if (GameUtils.isEnemy(entity, ownerGP.getTeam())) {
                                GameUtils.getObject((LivingEntity) entity).addEffect(new PotionEffect(PotionEffectType.SLOW, duration, 3));
                            }
                        });
                        block.spawn();
                    }
                    block.setStopped(true);
                    block.dieLater(100);
                    block.setNoGravity(true);
                    block.setVelocity(MathUtils.ZEROVECTOR);
                }
            }
            owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_HOSTILE_DEATH, 1.07f, 1);
        });
        smartFallingBlock.setOnHitEntity((Entity entity) -> {
            Vector at = entity.getLocation().toBlockLocation().toVector();
            smartFallingBlock.locX = at.x;
            smartFallingBlock.locY = at.y;
            smartFallingBlock.locZ = at.z;
            smartFallingBlock.getOnHitGround().run();
        });

        smartFallingBlock.setOnEnter((Entity entity) -> {
            if (GameUtils.isEnemy(entity, ownerGP.getTeam())) {
                GameUtils.getObject((LivingEntity) entity).addEffect(new PotionEffect(PotionEffectType.SLOW, duration, 3));
            }
        });
    }
}
