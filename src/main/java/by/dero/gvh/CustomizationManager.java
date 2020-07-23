package by.dero.gvh;

import by.dero.gvh.model.CustomizationInfo;
import lombok.Getter;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class CustomizationManager {
    @Getter
    private final Map<String, CustomizationInfo> customizations = new HashMap<>();

    public CustomizationManager() {
        // register heads
        register(CustomizationInfo.builder()
                .hero("all")
                .material(Material.DIAMOND)
                .displayName("Падающие головы")
                .cost(50)
                .build());
    }

    public void register(CustomizationInfo customization) {
        customizations.put(customization.getName(), customization);
    }
}
