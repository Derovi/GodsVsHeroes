package by.dero.gvh.lobby.monuments;

import by.dero.gvh.Plugin;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.lobby.interfaces.TotemInterface;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

public class Totem {
	CraftArmorStand[] heads;
	private final int animTime = 68;
	
	public Totem(Location at) {
		for (int i = 0; i < 3; i++) {
			EntityArmorStand handle = new EntityArmorStand(((CraftWorld) at.getWorld()).world);
			handle.setPosition(at.x, at.y - handle.getHeadHeight() + 0.85 + i * 0.65, at.z);
			CraftArmorStand stand = (CraftArmorStand) handle.getBukkitEntity();
			GameUtils.setInvisibleFlags(stand);
			stand.setHeadPose(new EulerAngle(0, 0, Math.PI / 2));
			stand.setHelmet(new ItemStack(Material.BONE));
			handle.world.addEntity(handle, CreatureSpawnEvent.SpawnReason.CUSTOM);
			Lobby.getInstance().getMonumentManager().getOnClick().put(handle.uniqueID, (p) ->
					new TotemInterface(Lobby.getInstance().getInterfaceManager(), p).open());
		}
		for (int i = 1; i <= 2; i++) {
			EntityArmorStand handle = new EntityArmorStand(((CraftWorld) at.getWorld()).world);
			handle.setPosition(at.x - 0.2, at.y - handle.getHeadHeight() + 0.75 + i * 0.65, at.z);
			CraftArmorStand stand = (CraftArmorStand) handle.getBukkitEntity();
			GameUtils.setInvisibleFlags(stand);
			stand.setHeadPose(new EulerAngle(0, 0, Math.PI / 4));
			stand.setHelmet(new ItemStack(Material.BONE));
			handle.world.addEntity(handle, CreatureSpawnEvent.SpawnReason.CUSTOM);
			Lobby.getInstance().getMonumentManager().getOnClick().put(handle.uniqueID, (p) ->
					new TotemInterface(Lobby.getInstance().getInterfaceManager(), p).open());
		}
		for (int i = 1; i <= 2; i++) {
			EntityArmorStand handle = new EntityArmorStand(((CraftWorld) at.getWorld()).world);
			handle.setPosition(at.x + 0.25, at.y - handle.getHeadHeight() + 0.85 + i * 0.65, at.z);
			CraftArmorStand stand = (CraftArmorStand) handle.getBukkitEntity();
			GameUtils.setInvisibleFlags(stand);
			stand.setHeadPose(new EulerAngle(0, 0, Math.PI / 4 * 3));
			stand.setHelmet(new ItemStack(Material.BONE));
			handle.world.addEntity(handle, CreatureSpawnEvent.SpawnReason.CUSTOM);
			Lobby.getInstance().getMonumentManager().getOnClick().put(handle.uniqueID, (p) ->
					new TotemInterface(Lobby.getInstance().getInterfaceManager(), p).open());
		}
		heads = new CraftArmorStand[4];
		for (int i = 1; i <= 2; i++) {
			EntityArmorStand handle = new EntityArmorStand(((CraftWorld) at.getWorld()).world);
			handle.setSmall(true);
			handle.setPosition(at.x + 0.6, at.y - handle.getHeadHeight() + 0.2 + i * 0.75, at.z + 0.3);
			CraftArmorStand stand = (CraftArmorStand) handle.getBukkitEntity();
			GameUtils.setInvisibleFlags(stand);
			stand.setHeadPose(new EulerAngle(0, Math.PI, 0));
			stand.setHelmet(new ItemStack(Material.SKULL_ITEM));
			handle.world.addEntity(handle, CreatureSpawnEvent.SpawnReason.CUSTOM);
			heads[i-1] = stand;
			Lobby.getInstance().getMonumentManager().getOnClick().put(handle.uniqueID, (p) ->
					new TotemInterface(Lobby.getInstance().getInterfaceManager(), p).open());
		}
		for (int i = 1; i <= 2; i++) {
			EntityArmorStand handle = new EntityArmorStand(((CraftWorld) at.getWorld()).world);
			handle.setSmall(true);
			handle.setPosition(at.x - 0.5, at.y - handle.getHeadHeight() + 0.2 + i * 0.75, at.z + 0.3);
			CraftArmorStand stand = (CraftArmorStand) handle.getBukkitEntity();
			GameUtils.setInvisibleFlags(stand);
			stand.setHeadPose(new EulerAngle(0, Math.PI, 0));
			stand.setHelmet(new ItemStack(Material.SKULL_ITEM));
			handle.world.addEntity(handle, CreatureSpawnEvent.SpawnReason.CUSTOM);
			heads[i+1] = stand;
			Lobby.getInstance().getMonumentManager().getOnClick().put(handle.uniqueID, (p) ->
					new TotemInterface(Lobby.getInstance().getInterfaceManager(), p).open());
		}
		
		new BukkitRunnable() {
			int time = 0;
			@Override
			public void run() {
				time++;
				for (int i = 0; i < heads.length; i++) {
					if (i * 2 < time && time <= animTime - 8 + i * 2) {
						heads[i].setHeadPose(heads[i].getHeadPose().add(0, 0, MathUtils.PI2 / (animTime - 8)));
					}
				}
				
				if (time == animTime) {
					time = 0;
				}
				
			}
		}.runTaskTimer(Plugin.getInstance(), 0, 1);
	}
}
