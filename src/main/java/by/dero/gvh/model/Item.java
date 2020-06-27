package by.dero.gvh.model;

import by.dero.gvh.Cooldown;
import by.dero.gvh.Plugin;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static by.dero.gvh.utils.DataUtils.getPlayer;

public class Item {
    protected final Player owner;
    protected final String name;
    private final int level;
    protected final int team;

    public Cooldown getCooldown() {
        return cooldown;
    }

    protected Cooldown cooldown;

    protected final Set<UUID> summonedEntityIds = new HashSet<>();

    public Item(final String name, final int level, final Player owner) {
        this.name = name;
        this.level = level;
        this.owner = owner;

        team = getPlayer(owner.getName()).getTeam();
        cooldown = new Cooldown(getInfo().getCooldown());
        cooldown.makeReady();
    }

    public ItemStack getItemStack() {
        return getItemStack(name, getInfo());
    }

    public static ItemStack getItemStack(String name, ItemInfo info) {
        ItemStack itemStack = new ItemStack(info.getMaterial(), info.getAmount());
        ItemMeta itemMeta = itemStack.getItemMeta();
        for (ItemInfo.EnchantInfo enchantInfo : info.getEnchantments()) {
            System.out.println(enchantInfo.getName());
            itemMeta.addEnchant(Enchantment.getByName(enchantInfo.getName()), enchantInfo.getLevel(), enchantInfo.isVisible());
        }
        itemMeta.setDisplayName(info.getDisplayName());
        List<String> lore = new LinkedList<>(info.getLore());
        lore.add(Plugin.getInstance().getData().getItemNameToTag().get(name));
        itemMeta.setLore(lore);
        itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        itemMeta.addEnchant(Enchantment.DURABILITY, Integer.MAX_VALUE, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static String getTag(String name) {
        StringBuilder tag = new StringBuilder();
        int hashcode = Math.abs(name.hashCode());
        while (hashcode > 0) {
            tag.append('§').append(hashcode % 10);
            hashcode /= 10;
        }
        return tag.toString();
    }

    public Set<UUID> getSummonedEntityIds() {
        return summonedEntityIds;
    }

    public ItemDescription getDescription() {
        return Plugin.getInstance().getData().getItemDescription(name);
    }

    public ItemInfo getInfo() {
        return getDescription().getLevels().get(level);
    }

    public String getName() {
        return name;
    }
}
