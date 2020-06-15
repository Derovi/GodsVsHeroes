package by.dero.gvh.game;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.UnitClassDescription;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StandManager implements Listener {
    private static StandManager instance;
    private final Map<UUID, String> stands = new HashMap<>();
    private final double turnPerSec = 0.3;

    public void addStand(Location loc, String cl) {
        ArmorStand obj = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);

        UnitClassDescription classDescription = Plugin.getInstance().getData().getUnits().get(cl);

        for (String name : classDescription.getItemNames()) {
            if (!name.startsWith("default")) {
                continue;
            }
            Item item;
            try {
                item = (Item) Plugin.getInstance().getData().getItemNameToClass().
                        get(name).getConstructor(String.class, int.class, Player.class).
                        newInstance(name, 0, null);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            if (item.getDescription().isInvisible()) {
                continue;
            }
            int slot = item.getDescription().getSlot();
            EntityEquipment eq = obj.getEquipment();
            switch (slot) {
                case -1: eq.setHelmet(item.getItemStack()); break;
                case -2: eq.setChestplate(item.getItemStack()); break;
                case -3: eq.setLeggings(item.getItemStack()); break;
                case -4: eq.setBoots(item.getItemStack()); break;
            }
        }
        stands.put(obj.getUniqueId(), cl);
        new BukkitRunnable() {
            final Location st = loc.clone().add(0,0,0);
            double angle = 0;
            @Override
            public void run() {
                loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK,
                        st.clone().add(Math.cos(angle), 0, Math.sin(angle)),
                        1,0,0,0,0);

                loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK,
                        st.clone().add(Math.cos(angle + Math.PI), 0, Math.sin(angle + Math.PI)),
                        1,0,0,0,0);

                angle += Math.PI * turnPerSec / 5;
            }
        }.runTaskTimer(Plugin.getInstance(), 40, 2);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Player p = event.getPlayer();
        Entity ent = event.getRightClicked();
        if (stands.containsKey(ent.getUniqueId())) {
            GamePlayer player = Plugin.getInstance().getGame().getPlayers().get(p.getName());
            player.selectClass(stands.get(ent.getUniqueId()));
        }
    }

    public StandManager() {
        instance = this;
    }

    public static StandManager getInstance() {
        return instance;
    }
}
