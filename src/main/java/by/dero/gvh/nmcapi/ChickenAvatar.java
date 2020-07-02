package by.dero.gvh.nmcapi;

import by.dero.gvh.Plugin;
import net.minecraft.server.v1_12_R1.EntityChicken;
import net.minecraft.server.v1_12_R1.EnumMoveType;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.awt.*;

public class ChickenAvatar extends EntityChicken {
    private Player player;
    private double speed = 0.5;

    public ChickenAvatar(Player player) {
        super(((CraftWorld) player.getLocation().getWorld()).getHandle());
        this.player = player;
        setPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
        ((CraftEntity)player).getHandle().a(this, true);
        player.setGameMode(GameMode.ADVENTURE);
        PotionEffect effect = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0);
        player.addPotionEffect(effect);
        for (Player other : player.getWorld().getPlayers()) {
            other.hidePlayer(Plugin.getInstance(), player);
        }
        boundingBox.e += 1.05;
    }

    @Override
    public void r() { }

    @Override
    public void B_() {
        if (player.getVehicle() == null || !player.getVehicle().getUniqueId().equals(getUniqueID())) {
            die();
            return;
        }
        yaw = player.getLocation().getYaw();
        pitch = player.getLocation().getPitch();
        setHeadRotation(((CraftEntity) player).getHandle().getHeadRotation());
        double dx = player.getLocation().getDirection().getX() * speed;
        double dy = player.getLocation().getDirection().getY() * speed;
        double dz = player.getLocation().getDirection().getZ() * speed;
        super.move(EnumMoveType.SELF, dx, dy + motY, dz);
        this.motY = Math.max(0, motY - 0.03999999910593033D);
    }

    public void die() {
        player.setGameMode(GameMode.SURVIVAL);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        for (Player other : player.getWorld().getPlayers()) {
            other.showPlayer(Plugin.getInstance(), player);
        }
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
