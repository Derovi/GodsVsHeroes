package by.dero.gvh;

import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.MessagingUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Predicate;

public class AdviceManager {
    public static void sendAdvice(Player player, String adviceName) {
        MessagingUtils.sendSubtitle(Lang.get("advices." + adviceName), player, 5, 50, 5);
    }

    public static void sendAdvice(Player player, String adviceName, int delay, int period,
                                  Predicate<Player> stopPredicate) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (stopPredicate.test(player)) {
                    cancel();
                    return;
                }
                MessagingUtils.sendSubtitle(Lang.get("advices." + adviceName), player, 5, 50, 5);
            }
        }.runTaskTimer(Plugin.getInstance(), delay, period);
    }

    public static void sendAdvice(Player player, String adviceName, int delay, int period,
                                  Predicate<Player> stopPredicate, Predicate<Player> sendPredicate) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (stopPredicate.test(player)) {
                    cancel();
                    return;
                }
                if (sendPredicate.test(player)) {
                    MessagingUtils.sendSubtitle(Lang.get("advices." + adviceName), player, 5, 50, 5);
                }
            }
        }.runTaskTimer(Plugin.getInstance(), delay, period);
    }
}
