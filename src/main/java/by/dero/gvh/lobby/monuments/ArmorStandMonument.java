package by.dero.gvh.lobby.monuments;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.model.UnitClassDescription;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.scheduler.BukkitRunnable;

import static by.dero.gvh.model.Drawings.spawnMovingCircle;

public class ArmorStandMonument extends Monument {
    private final double turnPerSec = 0.3;
    private final double radius = 0.8;
    private int unlocktime = 0;
    private ArmorStand armorStand;
    private BukkitRunnable particles;
    private boolean loaded = false;

    public ArmorStandMonument(DirectedPosition position, String className, Player owner) {
        super(position, className, owner);
    }

    private void drawParticles() {
        particles = new BukkitRunnable() {
            final Location st = armorStand.getLocation().clone();
            double angle = 0;
            @Override
            public void run() {
                final PlayerInfo info = Lobby.getInstance().getPlayers().get(getOwner().getName()).getPlayerInfo();
                if (!info.isClassUnlocked(getClassName())) {
                    final double zxc = MathUtils.cos(Math.toRadians(70)) * 1.5;
                    spawnMovingCircle(st.clone().add(0, -0.15, 0),
                            1, zxc, 3, 0, Particle.FLAME, getOwner());

                    spawnMovingCircle(st.clone().add(0, -1, 0),
                            1, zxc, 3, 0, Particle.FLAME, getOwner());

                    spawnMovingCircle(st.clone().add(0, -1.85, 0),
                            1, zxc, 3, 0, Particle.FLAME, getOwner());
                    unlocktime = 240;
                } else
                if (unlocktime > 0) {
                    unlocktime -= 2;
                } else
                if (info.getSelectedClass().equals(getClassName())) {
                    getOwner().spawnParticle(Particle.DRAGON_BREATH,
                            st.clone().add(MathUtils.cos(angle) * radius,
                                    0,
                                    MathUtils.sin(angle) * radius),
                            0, 0, -0.04, 0);
                    getOwner().spawnParticle(Particle.DRAGON_BREATH,
                            st.clone().add(MathUtils.cos(angle + Math.PI) * radius,
                                    0,
                                    MathUtils.sin(angle + Math.PI) * radius),
                            0, 0, -0.04, 0);
                } else {
                    getOwner().spawnParticle(Particle.FIREWORKS_SPARK,
                            st.clone().add(MathUtils.cos(angle) * radius,
                                    0,
                                    MathUtils.sin(angle) * radius),
                            0, 0, 0, 0);
                    getOwner().spawnParticle(Particle.FIREWORKS_SPARK,
                            st.clone().add(MathUtils.cos(angle + Math.PI) * radius,
                                    0,
                                    MathUtils.sin(angle + Math.PI) * radius),
                            0, 0, 0, 0);
                }
                angle += Math.PI * turnPerSec / 5;
            }
        };
        particles.runTaskTimer(Plugin.getInstance(), 0, 2);
    }


    @Override
    public void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        final World at = Lobby.getInstance().getWorld();
        armorStand = (ArmorStand) at.spawnEntity(getPosition().toLocation(at), EntityType.ARMOR_STAND);
        armorStand.setCustomNameVisible(true);
        armorStand.setInvulnerable(true);

        final UnitClassDescription classDescription =
                Plugin.getInstance().getData().getClassNameToDescription().get(getClassName());
        for (final String name : classDescription.getItemNames()) {
            if (!name.startsWith("default")) {
                continue;
            }
            final Item item;
            try {
                item = Plugin.getInstance().getData().getItemNameToClass().
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
        if (!loaded) {
            return;
        }
        loaded = false;
        particles.cancel();
        armorStand.remove();
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }
}
