package by.dero.gvh;

import by.dero.gvh.model.Cosmetic;
import by.dero.gvh.model.CosmeticInfo;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class CosmeticManager {
    @Getter
    private final Map<String, CosmeticInfo> customizations = new HashMap<>();

    @Getter
    private final Map<UUID, Set<String>> playerCustomizations = new HashMap<>();

    public CosmeticManager() {
        // register heads
        register(CosmeticInfo.builder()
                .name("headDrop")
                .hero("all")
                .rarity(CosmeticInfo.Rarity.MYTHICAL)
                .material(Material.DIAMOND)
                .displayName("Падающие головы")
                .cost(50)
                .build());


        // warrior
        register(CosmeticInfo.builder()  // blood sword
                .name("bloodySword")
                .hero("warrior")
                .rarity(CosmeticInfo.Rarity.RARE)
                .material(Material.DIAMOND_SWORD)
                .displayName("§4Кровавый меч")
                .cost(50)
                .groupID(1)
                .nbt(new CosmeticInfo.NBT("weapons", "bloodyvengeance"))
                .build());

        register(CosmeticInfo.builder()  // victory sword
                .name("victorySword")
                .hero("warrior")
                .rarity(CosmeticInfo.Rarity.RARE)
                .material(Material.DIAMOND_SWORD)
                .displayName("§bМеч победы")
                .cost(50)
                .groupID(1)
                .build());
    }

    public void loadPlayer(Player player) {
        Set<String> customizations = new HashSet<>();
        for (Cosmetic customization :
                Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName()).getCosmetics().values()) {
            if (customization.isEnabled()) {
                customizations.add(customization.getName());
            }
        }
        playerCustomizations.put(player.getUniqueId(), customizations);
    }

    public boolean isEnabled(Player player, String customization) {
        if (playerCustomizations.containsKey(player.getUniqueId())) {
            return playerCustomizations.get(player.getUniqueId()).contains(customization);
        } else {
            HashMap<String, Cosmetic> customizations =
                    Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName()).getCosmetics();
            return customizations.containsKey(customization) && customizations.get(customization).isEnabled();
        }
    }

    public String getByGroup(Player player, int groupID) {
        Set<String> cosmetics;
        if (playerCustomizations.containsKey(player.getUniqueId())) {
            cosmetics = playerCustomizations.get(player.getUniqueId());
        } else {
            cosmetics = new HashSet<>();
            for (Cosmetic customization :
                    Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName()).getCosmetics().values()) {
                if (customization.isEnabled()) {
                    cosmetics.add(customization.getName());
                }
            }
        }
        for (String name : cosmetics) {
            if (customizations.get(name).getGroupID() == groupID) {
                return name;
            }
        }
        return null;
    }

    public void register(CosmeticInfo customization) {
        customizations.put(customization.getName(), customization);
    }
}
