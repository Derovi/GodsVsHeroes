package by.dero.gvh.model;

import by.dero.gvh.Cooldown;
import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.*;

public class Item {
    protected final Player owner;
    protected GamePlayer ownerGP = null;
    protected final String name;
    private final int level;
    private int team = -1;

    public static ItemStack getPane(String name) {
        ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 8);
        pane.setAmount(1);
        final ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(name);
        pane.setItemMeta(meta);
        return pane;
    }

    public Cooldown getCooldown() {
        return cooldown;
    }

    protected Cooldown cooldown;

    protected final Set<UUID> summonedEntityIds = new HashSet<>();

    public Item(final String name, final int level, final Player owner) {
        this.name = name;
        this.level = level;
        this.owner = owner;
        if (Game.getInstance() != null) {
            this.ownerGP = GameUtils.getPlayer(owner.getName());
        }

        cooldown = new Cooldown(getInfo().getCooldown());
        cooldown.makeReady();
    }

    public Set<UUID> getSummonedEntityIds() {
        return summonedEntityIds;
    }

    private ItemDescription description = null;
    public ItemDescription getDescription() {
        if (description == null) {
            description = Plugin.getInstance().getData().getItemDescription(name);
        }
        return description;
    }

    public ItemStack getItemStack() {
        return getInfo().getItemStack(owner);
    }

    public ItemInfo getInfo() {
        return getDescription().getLevels().get(level);
    }

    public String getName() {
        return name;
    }

    public int getTeam() {
        if (team == -1) {
            team = GameUtils.getPlayer(owner.getName()).getTeam();
        }
        return team;
    }

    public Player getOwner() {
        return owner;
    }

    public int getLevel() {
        return level;
    }
}
