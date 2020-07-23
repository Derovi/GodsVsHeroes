package by.dero.gvh.utils;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Minigame;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import ru.cristalix.core.display.IDisplayService;
import ru.cristalix.core.display.messages.MovementInput;

public class Stun {
    public static void stunEntity(LivingEntity p, int latency) {
        new PotionEffect(PotionEffectType.BLINDNESS, latency, 3).apply(p);
        boolean isPlayer = p instanceof Player;
        GamePlayer gp = null;
        if (isPlayer) {
            gp = GameUtils.getPlayer(p.getName());
            GameUtils.stunMessage(gp, latency);
            gp.setDisabled(true);
        }
        IDisplayService.get().sendMovementInput(p.getUniqueId(), MovementInput.builder().
                allowBackwards(false).allowForward(false).allowJumps(false).allowLeft(false).allowRight(false).
                allowShift(false).allowSprint(false).build());
        GamePlayer finalGp = gp;
        final BukkitRunnable runnable = new BukkitRunnable() {
//            final Location loc = p.getLocation().clone();
            int ticks = 0;
            @Override
            public void run() {
//                p.teleport(loc);
                if (ticks % 8 == 0) {
                    p.getWorld().spawnParticle(Particle.VILLAGER_ANGRY,
                            p.getEyeLocation().add(p.getLocation().getDirection().multiply(0.5)),
                            0, 0, 0, 0);
                }
                ticks += 2;
                if (ticks >= latency || (isPlayer && ((Player) p).getGameMode().equals(GameMode.SPECTATOR))) {
                    if (isPlayer) {
                        finalGp.setDisabled(false);
                        IDisplayService.get().sendMovementInput(p.getUniqueId(), MovementInput.builder().
                                allowBackwards(true).allowForward(true).allowJumps(true).allowLeft(true).allowRight(true).
                                allowShift(true).allowSprint(true).build());
                    }
                    this.cancel();
                }
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 2);
        Minigame.getInstance().getGame().getRunnables().add(runnable);
    }
}
