package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.KnifeThrowInfo;
import by.dero.gvh.nmcapi.throwing.ThrowingKnife;
import by.dero.gvh.utils.GameUtils;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

public class KnifeThrow extends Item implements PlayerInteractInterface {
    private final Material material;
    private final int meleeDamage;
    private final double damage;

    public KnifeThrow(String name, int level, Player owner) {
        super(name, level, owner);

        KnifeThrowInfo info = (KnifeThrowInfo) getInfo();
        damage = info.getDamage();
        meleeDamage = info.getMeleeDamage();
        material = info.getMaterial();

        owner.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(1024.0D);
        owner.saveData();
    }


    @Override
    public ItemStack getItemStack () {
        ItemStack sword = new ItemStack(material, 1);

        sword = setItemMeta(sword, name, getInfo());
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(sword);
        NBTTagCompound compound = nmsStack.hasTag() ? nmsStack.getTag() : new NBTTagCompound();
        NBTTagList modifiers = new NBTTagList();
        NBTTagCompound damage = new NBTTagCompound();
        damage.set("AttributeName", new NBTTagString("generic.attackDamage"));
        damage.set("Name", new NBTTagString("generic.attackDamage"));
        damage.set("Amount", new NBTTagInt(meleeDamage));
        damage.set("Operation", new NBTTagInt(0));
        damage.set("UUIDLeast", new NBTTagInt(894654));
        damage.set("UUIDMost", new NBTTagInt(2872));
        damage.set("Slot", new NBTTagString("mainhand"));
        modifiers.add(damage);
        compound.set("AttributeModifiers", modifiers);
        nmsStack.setTag(compound);
        sword = CraftItemStack.asBukkitCopy(nmsStack);

        return sword;
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        final ThrowingKnife knife = new ThrowingKnife(owner, material);
        owner.getWorld().playSound(owner.getLocation(), Sound.BLOCK_CLOTH_STEP,  1.07f, 1);
        knife.spawn();
        knife.setOnHitEntity(() -> {
            if (GameUtils.isEnemy(knife.getHoldEntity(), getTeam())) {
                GameUtils.damage(damage, (LivingEntity) knife.getHoldEntity(), owner);
                Location at = knife.getItemPosition().toLocation(owner.getWorld());
                at.getWorld().spawnParticle(Particle.BLOCK_CRACK, at.clone().add(0,0,0), 10,
                        new MaterialData(Material.REDSTONE_BLOCK));
            }
        });
        knife.setOnHitBlock(() -> {
            Location at = knife.getItemPosition().toLocation(owner.getWorld());
            owner.getWorld().spawnParticle(Particle.CRIT_MAGIC, at, 1);
            owner.getWorld().playSound(knife.getItemPosition().toLocation(owner.getWorld()),
                    Sound.BLOCK_FENCE_GATE_CLOSE, 1.07f, 1);
        });
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                knife.remove();
            }
        };
        runnable.runTaskLater(Plugin.getInstance(), cooldown.getDuration()-1);
        Game.getInstance().getRunnables().add(runnable);
    }
}
