package by.dero.gvh.minigame;

import by.dero.gvh.FlyingText;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Lang;
import by.dero.gvh.nmcapi.CustomLeash;
import by.dero.gvh.utils.Position;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
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

import java.util.*;

public class LiftManager implements Listener {
	private final HashMap<UUID, LinkedList<Entity>> lifting = new HashMap<>();
	private final HashSet<UUID> forced = new HashSet<>();
	private boolean loaded = false;

	private static final Vector[] adds = new Vector[] {
			new Vector(-2, 0, -2), new Vector(2, 0, 2), new Vector(2, 0, -2), new Vector(-2, 0, 2),
			new Vector(2, 0, 1), new Vector(1, 0, 2), new Vector(1, 0, -2), new Vector(-1, 0, -2),
			new Vector(-1, 0, 2), new Vector(-2, 0, 1), new Vector(2, 0, -1), new Vector(-2, 0, -1),
			new Vector(-2, 0, 0), new Vector(0, 0, 2), new Vector(0, 0, -2), new Vector(2, 0, 0),
			new Vector(-1, 0, -1), new Vector(1, 0, -1), new Vector(1, 0, 1), new Vector(-1, 0, 1),
			new Vector(-1, 0, 0), new Vector(0, 0, -1), new Vector(0, 0, 1), new Vector(1, 0, 0),
	};

	public static class Lift {
		private final Vector from;
		private final Vector to;
		private final double radius;
		
		public Lift(Vector from, Vector to, double radius) {
			this.from = from;
			this.to = to;
			this.radius = radius;
		}
		
		public boolean isInside(Location loc) {
			return loc.toVector().distance(from) <= radius;
		}
	}
	
	private FlyingText[] hints = null;
	private final ArrayList<Lift> lifts = new ArrayList<>();
	public void load() {
		if (loaded) {
			return;
		}
		loaded = true;
		World world = Minigame.getInstance().getGame().getWorld();
		hints = new FlyingText[lifts.size()];
		for (int i = 0; i < lifts.size(); i++) {
			hints[i] = new FlyingText(lifts.get(i).from.toLocation(world).add(0, 1, 0), Lang.get("hints.lift"));
		}
	}

	public void unload() {
		if (!loaded) {
			return;
		}
		loaded = false;
		lifts.clear();
		for (FlyingText hint : hints) {
			hint.unload();
		}
		hints = null;
	}

	public void addLift(by.dero.gvh.utils.Position start, Position destination, double radius) {
		lifts.add(new LiftManager.Lift(
				new Vector(start.getX(), start.getY(), start.getZ()),
				new Vector(destination.getX(), destination.getY(), destination.getZ()),
				radius
		));
	}

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
		if (!lifting.get(uuid).isEmpty() && !forced.contains(uuid) &&
				(event.getTo().getY() < event.getFrom().getY() || event.getTo().getY() - 1.5 > lifting.get(uuid).getLast().locY)) {
			forced.add(uuid);
			Entity ent = lifting.get(uuid).getFirst();
			Location loc = player.getLocation();
			player.setVelocity(new Vector(ent.locX - loc.x, (ent.locY - loc.y) / 2, ent.locZ - loc.z).multiply(0.5));
		}
		if (event.getFrom().getY() == event.getTo().getY() ||
				event.getFrom().getY() - (int)event.getFrom().getY() >= 0.1) {
			return;
		}
		Location loc = player.getLocation();
		Location good = null;
		for (Lift lift : lifts) {
			if (lift.isInside(loc)) {
				good = lift.to.toLocation(loc.getWorld());
				break;
			}
		}
		if (good == null) {
			return;
		}
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

		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.07f, 1);
	
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
	
	public ArrayList<Lift> getLifts() {
		return lifts;
	}
}
