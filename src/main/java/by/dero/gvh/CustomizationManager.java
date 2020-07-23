package by.dero.gvh;

import by.dero.gvh.model.Customization;
import by.dero.gvh.model.CustomizationInfo;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class CustomizationManager {
    @Getter
    private final Map<String, CustomizationInfo> customizations = new HashMap<>();

    @Getter
    private final Map<UUID, Set<String>> playerCustomizations = new HashMap<>();

    public CustomizationManager() {
        // register heads
        register(CustomizationInfo.builder()
                .hero("all")
                .material(Material.DIAMOND)
                .displayName("Падающие головы")
                .cost(50)
                .build());
    }

    public void loadPlayer(Player player) {
        Set<String> customizations = new HashSet<>();
        for (Customization customization :
                Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName()).getCustomizations().values()) {
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
            HashMap<String, Customization> customizations =
                    Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName()).getCustomizations();
            return customizations.containsKey(customization) && customizations.get(customization).isEnabled();
        }
    }

    public void register(CustomizationInfo customization) {
        customizations.put(customization.getName(), customization);
    }
}
