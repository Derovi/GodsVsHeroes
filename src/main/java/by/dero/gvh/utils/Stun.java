package by.dero.gvh.utils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class Stun implements Listener {
    private final HashMap<UUID, Long> players = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        Long time = Calendar.getInstance().getTimeInMillis() / 1000;
        if (players.getOrDefault(p.getUniqueId(), 0L) > time) {
            p.sendMessage("Вы не можете двигаться еще " + (players.get(p.getUniqueId()) - time) + "s");
        }
    }

    public void stunPlayer(LivingEntity p, int latency) {
        players.put(p.getUniqueId(), Calendar.getInstance().getTimeInMillis() / 1000 + latency);
        new PotionEffect(PotionEffectType.BLINDNESS, latency, 1).apply(p);
    }
}
