package by.dero.gvh.model.items;

import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.WebThrowInfo;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;

public class WebThrow extends Item implements PlayerInteractInterface {
    private int force;
    private int duration;

    public WebThrow(String name, int level, Player owner) {
        super(name, level, owner);
        WebThrowInfo info = ((WebThrowInfo) getInfo());
        force = info.getDuration();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        FallingBlock fallingBlock = Minigame.getInstance().getWorld().spawnFallingBlock(getOwner().getLocation(),
                new MaterialData(Material.WEB));
        fallingBlock.setVelocity(getOwner().getLocation().getDirection().normalize().multiply(force));
    }
}
