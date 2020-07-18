package by.dero.gvh.lobby.monuments;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.model.ItemInfo;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.model.UnitClassDescription;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Bukkit;
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

    public ArmorStandMonument(DirectedPosition position, String className) {
        super(position, className);
    }

    private void drawParticles() {
        particles = new BukkitRunnable() {
            final Location st = armorStand.getLocation().clone();
            double angle = 0;
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    final PlayerInfo info = Lobby.getInstance().getPlayers().get(player.getName()).getPlayerInfo();
                    if (!info.isClassUnlocked(getClassName())) {
                        final double zxc = MathUtils.cos(Math.toRadians(70)) * 1.5;
                        spawnMovingCircle(st.clone().add(0, 1.85, 0),
                                1, zxc, 3, 0, Particle.FLAME, player);

                        spawnMovingCircle(st.clone().add(0, 1, 0),
                                1, zxc, 3, 0, Particle.FLAME, player);

                        spawnMovingCircle(st.clone().add(0, 0.15, 0),
                                1, zxc, 3, 0, Particle.FLAME, player);
                        unlocktime = 240;
                    } else if (unlocktime > 0) {
                        unlocktime -= 2;
                    } else if (info.getSelectedClass().equals(getClassName())) {
                        player.spawnParticle(Particle.DRAGON_BREATH,
                                st.clone().add(MathUtils.cos(angle) * radius,
                                        2,
                                        MathUtils.sin(angle) * radius),
                                0, 0, -0.04, 0);
                        player.spawnParticle(Particle.DRAGON_BREATH,
                                st.clone().add(MathUtils.cos(angle + Math.PI) * radius,
                                        2,
                                        MathUtils.sin(angle + Math.PI) * radius),
                                0, 0, -0.04, 0);
                    } else {
                        player.spawnParticle(Particle.FIREWORKS_SPARK,
                                st.clone().add(MathUtils.cos(angle) * radius,
                                        2,
                                        MathUtils.sin(angle) * radius),
                                0, 0, 0, 0);
                        player.spawnParticle(Particle.FIREWORKS_SPARK,
                                st.clone().add(MathUtils.cos(angle + Math.PI) * radius,
                                        2,
                                        MathUtils.sin(angle + Math.PI) * radius),
                                0, 0, 0, 0);
                    }
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
        armorStand.setCustomNameVisible(false);

        final UnitClassDescription classDescription =
                Plugin.getInstance().getData().getClassNameToDescription().get(getClassName());
        for (final String name : classDescription.getItemNames()) {
            if (!name.startsWith("default")) {
                continue;
            }
            final ItemInfo info = Plugin.getInstance().getData().getItems().get(name).getLevels().get(0);
            if (info.getDescription().isInvisible()) {
                continue;
            }
            final int slot = info.getDescription().getSlot();
            final EntityEquipment eq = armorStand.getEquipment();
            switch (slot) {
                case -1: eq.setHelmet(info.getItemStack(null)); break;
                case -2: eq.setChestplate(info.getItemStack(null)); break;
                case -3: eq.setLeggings(info.getItemStack(null)); break;
                case -4: eq.setBoots(info.getItemStack(null)); break;
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
