package by.dero.gvh.nmcapi;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.utils.GameUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.EntityChicken;
import net.minecraft.server.v1_12_R1.EnumMoveType;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ChickenAvatar extends EntityChicken {
    private Player player;
    private final GamePlayer gamePlayer;
    private double playerHealth;
    private double speed = 0.5;
    @Getter
    @Setter
    private int heal;
    private int savedHealth;

    public ChickenAvatar(Player player) {
        super(((CraftWorld) player.getLocation().getWorld()).getHandle());
        this.player = player;
        this.gamePlayer = GameUtils.getPlayer(player.getName());
        gamePlayer.hideInventory();
        setPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
        ((CraftEntity)player).getHandle().a(this, true);
        PotionEffect effect = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0);
        player.addPotionEffect(effect);
        playerHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        savedHealth = (int) (player.getHealth());
        player.setHealth(4);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(4);
        boundingBox.e += 1.05;
    }

    @Override
    public void r() {}

    @Override
    public void B_() {
        if (player.isDead() || player.getVehicle() == null || !player.getVehicle().getUniqueId().equals(getUniqueID())) {
            finish();
            return;
        }
        setHealth((float) player.getHealth());
        yaw = player.getLocation().getYaw();
        pitch = player.getLocation().getPitch();
        setHeadRotation(((CraftEntity) player).getHandle().getHeadRotation());
        double dx = player.getLocation().getDirection().getX() * speed;
        double dy = player.getLocation().getDirection().getY() * speed;
        double dz = player.getLocation().getDirection().getZ() * speed;
        super.move(EnumMoveType.SELF, dx + motX, dy + motY, dz + motZ);
        this.motY = Math.max(0, motY - 0.03999999910593033D);
        this.motX *= 0.9800000190734863D;
        this.motY *= 0.9800000190734863D;
        this.motZ *= 0.9800000190734863D;
    }

    public void finish() {
        if (dead) {
            return;
        }
        player.leaveVehicle();
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(playerHealth);
        player.setHealth(Math.min(playerHealth, savedHealth + heal));
        gamePlayer.showInventory();
        super.die();
    }

    public void spawn() {
        getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));
        getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
