package by.dero.gvh.lobby.monuments;

import by.dero.gvh.FlyingText;
import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.model.UnitClassDescription;
import by.dero.gvh.utils.DirectedPosition;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static by.dero.gvh.model.Drawings.drawCircle;
import static by.dero.gvh.model.Drawings.spawnMovingCircle;

public class ArmorStandMonument extends Monument {
    private final double turnPerSec = 0.3;
    private final double radius = 0.8;
    private int unlocktime = 0;
    private ArmorStand armorStand;
    private final List<BukkitRunnable> runnables = new ArrayList<>();

    public ArmorStandMonument(DirectedPosition position, String className, Player owner) {
        super(position, className, owner);
    }

    private void drawParticles() {
        final BukkitRunnable runnable = new BukkitRunnable() {
            final Location st = armorStand.getLocation().clone();
            double angle = 0;
            @Override
            public void run() {
                final PlayerInfo info = Lobby.getInstance().getPlayers().get(getOwner().getName()).getPlayerInfo();
                if (!info.isClassUnlocked(getClassName())) {
                    final double zxc = Math.cos(Math.toRadians(70)) * 1.5;
                    spawnMovingCircle(st.clone().add(0, -0.15,0),
                            1, zxc, 3, 0, Particle.FLAME, getOwner());

                    spawnMovingCircle(st.clone().add(0, -1,0),
                            1, zxc, 3, 0, Particle.FLAME, getOwner());

                    spawnMovingCircle(st.clone().add(0, -1.85,0),
                            1, zxc, 3, 0, Particle.FLAME, getOwner());
                    unlocktime = 240;
                } else
                if (unlocktime > 0) {
                    unlocktime -= 2;
                } else
                if (info.getSelectedClass().equals(getClassName())) {
                    getOwner().spawnParticle(Particle.DRAGON_BREATH,
                            st.clone().add(Math.cos(angle)*radius, 0, Math.sin(angle)*radius),
                            0,0,-0.05,0);
                    getOwner().spawnParticle(Particle.DRAGON_BREATH,
                            st.clone().add(Math.cos(angle + Math.PI)*radius, 0, Math.sin(angle + Math.PI)*radius),
                            0,0, -0.05,0);
                } else {
                    getOwner().spawnParticle(Particle.FIREWORKS_SPARK,
                            st.clone().add(Math.cos(angle)*radius, 0, Math.sin(angle)*radius),
                            0,0,0,0);
                    getOwner().spawnParticle(Particle.FIREWORKS_SPARK,
                            st.clone().add(Math.cos(angle + Math.PI)*radius, 0, Math.sin(angle + Math.PI)*radius),
                            0,0, 0,0);
                }
                angle += Math.PI * turnPerSec / 5;
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 2);
        runnables.add(runnable);
    }


    @Override
    public void load() {
        final World at = Lobby.getInstance().getWorld();
        armorStand = (ArmorStand) at.spawnEntity(getPosition().toLocation(at), EntityType.ARMOR_STAND);
        armorStand.setCustomNameVisible(true);


        final UnitClassDescription classDescription =
                Plugin.getInstance().getData().getClassNameToDescription().get(getClassName());
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
        drawParticles();
    }

    @Override
    public void unload() {
        for (BukkitRunnable runnable : runnables) {
            runnable.cancel();
        }
        armorStand.remove();
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }
}
