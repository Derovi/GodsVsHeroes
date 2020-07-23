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
                .material(Material.DIAMOND)
                .displayName("Падающие головы")
                .cost(50)
                .groupID(0)
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

    public void register(CosmeticInfo customization) {
        customizations.put(customization.getName(), customization);
    }
}
