package by.dero.gvh.utils;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.Damaging;
import by.dero.gvh.nmcapi.NMCUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.AuthorNagException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Dwelling {
	public static void attackOffhand(LivingEntity rc, Player player) {
		ItemStack item = player.getInventory().getItemInOffHand();
		try {
			if (player.getInventory().getItemInMainHand() != null &&
					(player.getInventory().getItemInMainHand().getType() == Material.BOW ||
							player.getPlayer().getInventory().getItemInMainHand().getType() == Material.SHIELD ||
							player.getInventory().getItemInMainHand().getType().name().matches("TRIDENT"))) {
				return;
			}
		} catch (IllegalArgumentException | NullPointerException | AuthorNagException ignored) {
		}
		if (rc != null && !rc.isDead()) {
			if (!(rc instanceof Player) || ((Player) rc).getGameMode() != GameMode.CREATIVE) {
				if (rc.getType() != EntityType.ARMOR_STAND &&
						rc.getType() != EntityType.ITEM_FRAME &&
						rc.getType() != EntityType.MINECART &&
						rc.getType() != EntityType.MINECART_CHEST &&
						rc.getType() != EntityType.MINECART_COMMAND &&
						rc.getType() != EntityType.MINECART_FURNACE &&
						rc.getType() != EntityType.MINECART_HOPPER &&
						rc.getType() != EntityType.MINECART_MOB_SPAWNER &&
						rc.getType() != EntityType.MINECART_TNT) {
					GamePlayer gp = GameUtils.getPlayer(player.getName());
					Item it = gp.getItems().getOrDefault(NMCUtils.getNBT(item).getString("custom"), null);
					
					if (GameUtils.isEnemy(rc, gp.getTeam())) {
						GameUtils.damage(((Damaging)it.getInfo()).getDamage(), rc, player, true);
					}
					player.getLocation().getWorld().spawnParticle(Particle.CRIT, player.getLocation().add(0.0D, 1.0D, 0.0D), 1);
				}
			}
		}
	}
	
	public static Object prepareVanillaPacket(String packetName, Object... objects) {
		int objectIndex = 0;
		Object outputObject = null;
		
		try {
			outputObject = Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf(".") + 1) + "." + packetName).newInstance();
			
			for(Iterator var5 = getDeclaredFields(Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf(".") + 1) + "." + packetName)).iterator(); var5.hasNext(); ++objectIndex) {
				Field field = (Field)var5.next();
				rewriteField(outputObject, field.getName(), objects[objectIndex]);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | ClassNotFoundException var7) {
		}
		
		return outputObject;
	}
	
	public static List<Field> getDeclaredFields(Class clazz) {
		List<Field> fields = new ArrayList(Arrays.asList(clazz.getDeclaredFields()));
		if (clazz.getSuperclass() != null) {
			fields.addAll(getDeclaredFields(clazz.getSuperclass()));
		}
		
		return fields;
	}
	
	public static void rewriteField(Object packet, String key, Object value) {
		try {
			Field field = packet.getClass().getDeclaredField(key);
			field.setAccessible(true);
			field.set(packet, value);
		} catch (IllegalAccessException | NoSuchFieldException var5) {
		}
	}
	
	public static void sendPacket(Player player, Object packet) {
		if (player.isOnline()) {
			try {
				Object craftPlayer = Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf(".") + 1) + ".entity.CraftPlayer").cast(player);
				Object handle = getFieldInstance(craftPlayer, "entity");
				Object playerConnection = getFieldInstance(handle, "playerConnection");
				invokeMethod(playerConnection, new Class[]{Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf(".") + 1) + ".Packet")}, packet);
			} catch (ClassNotFoundException var6) {
			}
		}
	}
	
	public static Object getFieldInstance(Object instance, String fieldName) {
		try {
			Field field = getDeclaredField(getDeclaredFields(instance.getClass()), fieldName);
			field.setAccessible(true);
			return field.get(instance);
		} catch (IllegalAccessException var4) {
			return null;
		}
	}
	
	public static void invokeMethod(Object instance, Class<?>[] classes, Object... values) {
		try {
			Method method = instance.getClass().getDeclaredMethod("sendPacket", classes);
			method.setAccessible(true);
			method.invoke(instance, values);
		} catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException var5) {
		}
	}
	
	public static Field getDeclaredField(List<Field> fields, String fieldName) {
		return fields.stream().filter((field) -> field.getName().equalsIgnoreCase(fieldName)).findFirst().orElse(null);
	}
}
