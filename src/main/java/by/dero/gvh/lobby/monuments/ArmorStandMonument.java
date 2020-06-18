package by.dero.gvh.lobby.monuments;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.UnitClassDescription;
import by.dero.gvh.utils.Position;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

import static by.dero.gvh.utils.DataUtils.getPlayer;
import static by.dero.gvh.utils.MessagingUtils.getNormal;

public class ArmorStandMonument extends Monument {
    private final double turnPerSec = 0.3;
    private final double radius = 0.8;
    private ArmorStand armorStand;

    public ArmorStandMonument(Position position, String className, Player owner) {
        super(position, className, owner);
    }

    @Override
    public void load() {
        final World at = Lobby.getInstance().getWorld();
        armorStand = (ArmorStand) at.spawnEntity(getPosition().toLocation(at), EntityType.ARMOR_STAND);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(getNormal(ChatColor.AQUA + "RMC to select: " + getClassName()));

        final Location loc = armorStand.getLocation();
        final UnitClassDescription classDescription = Plugin.getInstance().getData().getClassNameToDescription().get(getClassName());
        for (final String name : classDescription.getItemNames()) {
            if (!name.startsWith("default")) {
                continue;
            }
            final Item item;
            try {
                item = (Item) Plugin.getInstance().getData().getItemNameToClass().
                        get(name).getConstructor(String.class, int.class, Player.class).
                        newInstance(name, 0, getOwner());
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            if (item.getDescription().isInvisible()) {
                continue;
            }
            final int slot = item.getDescription().getSlot();
            final EntityEquipment eq = armorStand.getEquipment();
            switch (slot) {
                case -1: eq.setHelmet(item.getItemStack()); break;
                case -2: eq.setChestplate(item.getItemStack()); break;
                case -3: eq.setLeggings(item.getItemStack()); break;
                case -4: eq.setBoots(item.getItemStack()); break;
            }
        }

        new BukkitRunnable() {
            final Location st = loc.clone().subtract(0, 2, 0);
            double angle = 0;
            final HashMap<UUID, UUID> active = Lobby.getInstance().getMonumentManager().getActive();
            @Override
            public void run() {
                if (active.getOrDefault(getOwner().getUniqueId(), new UUID(0,0)).equals(armorStand.getUniqueId())) {
                    at.spawnParticle(Particle.DRAGON_BREATH,
                            st.clone().add(Math.cos(angle)*radius, 0, Math.sin(angle)*radius),
                            0,0,-0.05,0);
                    at.spawnParticle(Particle.DRAGON_BREATH,
                            st.clone().add(Math.cos(angle + Math.PI)*radius, 0, Math.sin(angle + Math.PI)*radius),
                            0,0, -0.05,0);
                } else {
                    at.spawnParticle(Particle.FIREWORKS_SPARK,
                            st.clone().add(Math.cos(angle)*radius, 0, Math.sin(angle)*radius),
                            0,0,0,0);
                    at.spawnParticle(Particle.FIREWORKS_SPARK,
                            st.clone().add(Math.cos(angle + Math.PI)*radius, 0, Math.sin(angle + Math.PI)*radius),
                            0,0, 0,0);
                }

                angle += Math.PI * turnPerSec / 5;
            }
        }.runTaskTimer(Plugin.getInstance(), 60, 2);
    }

    @Override
    public void unload() {
        armorStand.remove();
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }
}
