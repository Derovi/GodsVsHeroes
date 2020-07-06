package by.dero.gvh.minigame;

import by.dero.gvh.Plugin;
import by.dero.gvh.nmcapi.CustomLeash;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftChicken;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.UUID;

public class LiftManager implements Listener {
	private final HashMap<UUID, LinkedList<Entity>> lifting = new HashMap<>();
	private final HashSet<UUID> forced = new HashSet<>();

	private static final Vector[] adds = new Vector[] {
			new Vector(-2, 0, -2), new Vector(2, 0, 2), new Vector(2, 0, -2), new Vector(-2, 0, 2),
			new Vector(2, 0, 1), new Vector(1, 0, 2), new Vector(1, 0, -2), new Vector(-1, 0, -2),
			new Vector(-1, 0, 2), new Vector(-2, 0, 1), new Vector(2, 0, -1), new Vector(-2, 0, -1),
			new Vector(-2, 0, 0), new Vector(0, 0, 2), new Vector(0, 0, -2), new Vector(2, 0, 0),
			new Vector(-1, 0, -1), new Vector(1, 0, -1), new Vector(1, 0, 1), new Vector(-1, 0, 1),
			new Vector(-1, 0, 0), new Vector(0, 0, -1), new Vector(0, 0, 1), new Vector(1, 0, 0),
	};

	@EventHandler
	public void activateLift(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		if (!lifting.containsKey(uuid)) {
			lifting.put(uuid, new LinkedList<>());
		}
		if (event.getTo().getY() == event.getFrom().getY()) {
			removeRopes(uuid);
		}
		if (!lifting.get(uuid).isEmpty() && !forced.contains(uuid) && event.getTo().getY() < event.getFrom().getY()) {
			forced.add(uuid);
			Entity ent = lifting.get(uuid).getFirst();
			Location loc = player.getLocation();
			player.setVelocity(new Vector(ent.locX - loc.x, ent.locY - loc.y, ent.locZ - loc.z).multiply(0.4));
		}
		if (event.getFrom().getY() != event.getTo().getY() &&
				event.getFrom().getY() - (int)event.getFrom().getY() < 0.1 &&
				player.getLocation().add(0, -1, 0).getBlock().getType().equals(Material.SEA_LANTERN)) {

			Location loc = player.getLocation();
			Location good = loc;
			boolean hasAny = true;
			while (hasAny) {
				hasAny = false;
				for (Vector add : adds) {
					Location cur = loc.clone().add(add);
					if (!cur.getBlock().getType().equals(Material.AIR)) {
						hasAny = true;
						good = cur;
					}
				}
				loc.add(0, 1, 0);
			}
			good = good.toBlockLocation().add(0, 1, 0);
			EntityPlayer pl = ((CraftPlayer) player).getHandle();
			EntityChicken chicken = new EntityChicken(pl.world);

			chicken.setNoAI(true);
			chicken.setNoGravity(true);
			chicken.setInvisible(true);
			chicken.noclip = true;
			chicken.collides = false;
			chicken.invulnerable = true;
			((CraftChicken) chicken.getBukkitEntity()).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000, 0));

			chicken.setPosition(good.x + 0.5, good.y, good.z + 0.5);
			chicken.getBukkitEntity().setMetadata("custom", new FixedMetadataValue(Plugin.getInstance(), ""));

			CustomLeash leash = CustomLeash.a(pl.world, new BlockPosition(good.x, good.y, good.z));

			chicken.world.addEntity(chicken, CreatureSpawnEvent.SpawnReason.CUSTOM);
			pl.playerConnection.sendPacket(new PacketPlayOutAttachEntity(chicken, pl));
			LinkedList<Entity> list = lifting.get(uuid);
			list.add(chicken);
			list.add(leash);
			player.setVelocity(new Vector(0, Math.cbrt(good.y - player.getLocation().y) * 0.7, 0));

			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.7f, 1);
		}
	}

	public void removeRopes(UUID uuid) {
		forced.remove(uuid);
		LinkedList<Entity> list = lifting.get(uuid);
		while (!list.isEmpty()){
			Entity e = list.getFirst();
			e.die();
			list.removeFirst();
		}
	}
}
