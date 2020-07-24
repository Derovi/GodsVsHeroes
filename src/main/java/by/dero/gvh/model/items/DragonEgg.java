package by.dero.gvh.model.items;

import by.dero.gvh.FlyingText;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.Lang;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.model.itemsinfo.DragonEggInfo;
import by.dero.gvh.nmcapi.DragonEggEntity;
import by.dero.gvh.utils.SafeRunnable;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DragonEgg extends Item implements DoubleSpaceInterface {
    private final int duration;

    public DragonEgg(String name, int level, Player owner) {
        super(name, level, owner);
        DragonEggInfo info = (DragonEggInfo) getInfo();
        duration = info.getDuration();
    }

    @Override
    public void onDoubleSpace() {
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        DragonEggEntity egg = new DragonEggEntity(owner);
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1);
        String def = "||||||||||";
        FlyingText text = new FlyingText(owner.getEyeLocation(), def);
        SafeRunnable textRun = new SafeRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= duration) {
                    this.cancel();
                } else {
                    int prog = ticks * 10 / duration;
                    String pref = Lang.get("commands." + (char)('1' + ownerGP.getTeam())).substring(0, 2);
                    text.setText(pref + def.substring(0, prog) + "§f" + def.substring(prog+1));
                    ticks += 5;
                }
            }
        };
        textRun.runTaskTimer(Plugin.getInstance(), 5, 5);
        Game.getInstance().getRunnables().add(textRun);
        egg.spawn();
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                egg.finish();
                text.unload();
            }
        };
        runnable.runTaskLater(Plugin.getInstance(), duration);
        Game.getInstance().getRunnables().add(runnable);
    }
}
