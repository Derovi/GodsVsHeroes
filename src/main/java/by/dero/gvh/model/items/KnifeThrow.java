package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.KnifeThrowInfo;
import by.dero.gvh.model.itemsinfo.SwordThrowInfo;
import by.dero.gvh.nmcapi.throwing.ThrowingKnife;
import by.dero.gvh.nmcapi.throwing.ThrowingSword;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;

public class KnifeThrow extends Item implements PlayerInteractInterface {
    private final KnifeThrowInfo info;

    public KnifeThrow(String name, int level, Player owner) {
        super(name, level, owner);
        info = (KnifeThrowInfo) getInfo();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        final ThrowingKnife knife = new ThrowingKnife(owner, info.getMaterial());
        knife.spawn();
        knife.setOnHitEntity(() -> {
            if (GameUtils.isEnemy(knife.getHoldEntity(), getTeam())) {
                GameUtils.damage(info.getDamage(), (LivingEntity) knife.getHoldEntity(), owner);
                Location at = knife.getItemPosition().toLocation(owner.getWorld());
                at.getWorld().spawnParticle(Particle.BLOCK_CRACK, at.clone().add(0,0,0), 50,
                        new MaterialData(Material.REDSTONE_BLOCK));
            }
        });
        knife.setOnHitBlock(new Runnable() {
            @Override
            public void run() {
                Location at = knife.getItemPosition().toLocation(owner.getWorld());
                owner.spawnParticle(Particle.CRIT, at, 1);
            }
        });
    }
}
