package by.dero.gvh.model;

import by.dero.gvh.Plugin;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static by.dero.gvh.utils.DataUtils.getPlayer;

public class Item {
    private Player owner;
    private String name;
    private int level;
    protected final int team;

    private final Set<UUID> summonedEntityIds = new HashSet<>();

    public Item(String name, int level, Player owner) {
        this.name = name;
        this.level = level;
        this.owner = owner;
        team = getPlayer(owner.getName()).getTeam();
    }

    public ItemStack getItemStack() {
        return getItemStack(name, getInfo());
    }

    public static ItemStack getItemStack(String name, ItemInfo info) {
        ItemStack itemStack = new ItemStack(info.getMaterial(), info.getAmount());
        ItemMeta itemMeta = itemStack.getItemMeta();
        for (ItemInfo.EnchantInfo enchantInfo : info.getEnchantments()) {
            System.out.println(enchantInfo.getKey().getKey());
            itemMeta.addEnchant(Enchantment.getByKey(enchantInfo.getKey()), enchantInfo.getLevel(), enchantInfo.isVisible());
        }
        itemMeta.setDisplayName(info.getDisplayName());
        List<String> lore = info.getLore();
        // add tag as last line of lore
        lore.add(Plugin.getInstance().getData().getItemNameToTag().get(name));
        itemMeta.setLore(lore);
        itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        itemMeta.addEnchant(Enchantment.DURABILITY, 10, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static String getTag(String name) {
        StringBuilder tag = new StringBuilder();
        int hashcode = Math.abs(name.hashCode());
        while (hashcode > 0) {
            tag.append(hashcode % 10);
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

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
