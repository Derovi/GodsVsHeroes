package by.dero.gvh.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter @Setter
public class CustomizationContext {
    private Player player;
    private String className;
}
