package by.dero.gvh.nmcapi;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.utils.GameUtils;
import net.minecraft.server.v1_12_R1.EntityFallingBlock;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DragonEggEntity extends EntityFallingBlock {
    private GamePlayer player;

    public DragonEggEntity(Player player) {
        super(((CraftWorld) player.getLocation().getWorld()).getHandle(),
                player.getLocation().getX(),
                player.getLocation().getY(),
                player.getLocation().getZ(),
                CraftMagicNumbers.getBlock(Material.DRAGON_EGG).fromLegacyData(0));
        setInvulnerable(true);
        setNoGravity(true);
        player.setInvulnerable(false);
        PotionEffect effect = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0);
        player.addPotionEffect(effect);
        ((CraftPlayer) player).getHandle().a(this, true);
        for (Player other : player.getWorld().getPlayers()) {
            other.hidePlayer(Plugin.getInstance(), player);
        }
        this.player = GameUtils.getPlayer(player.getName());
        this.player.hideInventory();
    }

    @Override
    public void B_() {
        if (player.getPlayer().getVehicle() == null ||
                !player.getPlayer().getVehicle().getUniqueId().equals(getUniqueID())) {
            finish();
        }
    }

    public void finish() {
        player.showInventory();
        player.getPlayer().setInvulnerable(false);
        player.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
        for (Player other : player.getPlayer().getWorld().getPlayers()) {
            other.showPlayer(Plugin.getInstance(), player.getPlayer());
        }
        die();
    }

    public void spawn() {
        getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public GamePlayer getPlayer() {
        return player;
    }

    public void setPlayer(GamePlayer player) {
        this.player = player;
    }
}
