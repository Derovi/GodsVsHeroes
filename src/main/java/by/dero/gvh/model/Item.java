package by.dero.gvh.model;

import by.dero.gvh.Cooldown;
import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Item {
    protected final Player owner;
    protected GamePlayer ownerGP = null;
    protected final String name;
    private final int level;
    private int team = -1;

    private static ItemStack pane = null;
    public static ItemStack getPane(String name) {
        if (pane == null) {
            pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 8);
            pane.setAmount(1);
        }
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
        return getInfo().getItemStack(new CustomizationContext(owner, ownerGP.getClassName()));
    }

    public ItemInfo getInfo() {
        return getDescription().getLevels().get(level);
    }

    public String getName() {
        return name;
    }

    public int getTeam() {
        if (team == -1) {
            team = ownerGP.getTeam();
        }
        return team;
    }

    public Player getOwner() {
        return owner;
    }
    
    public GamePlayer getOwnerGP() {
        return ownerGP;
    }
    
    public int getLevel() {
        return level;
    }
}
