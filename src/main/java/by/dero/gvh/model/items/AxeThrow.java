package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.AxeThrowInfo;
import by.dero.gvh.nmcapi.throwing.ThrowingAxe;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
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

public class AxeThrow extends Item implements PlayerInteractInterface, InfiniteReplenishInterface {
    private final Material material;
    private final double damage;
    private final int meleeDamage;
    public AxeThrow(String name, int level, Player owner) {
        super(name, level, owner);
        final AxeThrowInfo info = (AxeThrowInfo) getInfo();
        damage = info.getDamage();
        material = info.getMaterial();
        meleeDamage = info.getMeleeDamage();

        owner.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(1024.0D);
        owner.saveData();
    }

    // TODO damage
    /*@Override
    public ItemStack getItemStack () {
        ItemStack axe = new ItemStack(material, 1);

        axe = setItemMeta(axe, name, getInfo());
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(axe);
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
        axe = CraftItemStack.asBukkitCopy(nmsStack);

        return axe;
    }*/

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        final ThrowingAxe axe = new ThrowingAxe(owner, getItemStack());

        final int slot = owner.getInventory().getHeldItemSlot();
        owner.getWorld().playSound(owner.getLocation(), Sound.BLOCK_CLOTH_STEP,  1.07f, 1);
        axe.spawn();
        axe.setOnHitEntity(() -> {
            if (GameUtils.isEnemy(axe.getHoldEntity(), getTeam())) {
                Location at = axe.getItemPosition().toLocation(owner.getWorld());
                at.getWorld().spawnParticle(Particle.BLOCK_CRACK, at, 50,
                        new MaterialData(Material.REDSTONE_BLOCK));
                GameUtils.damage(damage, (LivingEntity) axe.getHoldEntity(), owner);
            }
        });
        axe.setOnHitBlock(() -> {
            new BukkitRunnable() {
                double angle = 0;
                @Override
                public void run () {
                    if (axe.isRemoved()) {
                        this.cancel();
                        return;
                    }
                    angle += Math.PI / 10;
                    for (int i = 0; i < 2; i++) {
                        double al = angle + Math.PI * i;
                        Location at = axe.getItemPosition().toLocation(owner.getWorld()).
                                add(MathUtils.cos(al)*0.5, 1, MathUtils.sin(al)*0.5);
                        owner.spawnParticle(Particle.VILLAGER_HAPPY, at, 0, 0, 0, 0);
                    }
                }
            }.runTaskTimer(Plugin.getInstance(), 0, 5);
            owner.getWorld().playSound(axe.getItemPosition().toLocation(owner.getWorld()),
                    Sound.BLOCK_SHULKER_BOX_OPEN, 1.07f, 1);
        });
        axe.setOnOwnerPickUp(() -> {
            if (owner.getInventory().getItem(slot).getType().equals(Material.STAINED_GLASS_PANE)) {
                owner.getInventory().setItem(slot, getItemStack());
                owner.getInventory().getItem(slot).setAmount(1);
            }
            axe.remove();
        });
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                axe.remove();
            }
        };
        runnable.runTaskLater(Plugin.getInstance(), cooldown.getDuration());
        Game.getInstance().getRunnables().add(runnable);
    }
}
