package by.dero.gvh.minigame.ethercapture;

import by.dero.gvh.FlyingText;
import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.CollectorStructure;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.RewardManager;
import by.dero.gvh.model.Lang;
import by.dero.gvh.nmcapi.MovingCrystal;
import by.dero.gvh.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static by.dero.gvh.minigame.CollectorStructure.getChanging;
import static by.dero.gvh.minigame.CollectorStructure.getStages;

public class EtherCollector {
    private final int number;
    private Location location;
    private MovingCrystal crystal;
    private FlyingText captureIndicator;
    private final double maxHeight = 16;
    @Getter private int captureStatus = 0; // 0 to 36
    @Getter @Setter
    private int owner = 0;
    private SafeRunnable etherAdd;
    private final int etherDelay;
    private final int etherMineValue;
    private boolean loaded = false;

    public EtherCollector(IntPosition pos, int number) {
        setPosition(pos);
        this.number = number;
        EtherCaptureInfo info = EtherCapture.getInstance().getEtherCaptureInfo();
        etherDelay = info.getEtherMineDelay();
        etherMineValue = info.getEtherForCollector();
    }

    private final HashMap<GamePlayer, Double> rewards = new HashMap<>();
    private final HashMap<Integer, Integer> counter = new HashMap<>();
    public void update(List<GamePlayer> offenders) {
        if (offenders == null || offenders.size() == 0) {
            return;
        }

        counter.clear();
        for (GamePlayer gp : offenders) {
            counter.put(gp.getTeam(),counter.getOrDefault(gp.getTeam(), 0) + 1);
        }
        int leading = -1, val = -1;
        for (Map.Entry<Integer, Integer> entry : counter.entrySet()) {
            if (val < entry.getValue()) {
                val = entry.getValue();
                leading = entry.getKey();
            }
        }
        
        int leadval = val;
        val = val * 2 - offenders.size();
        char teamcode = Lang.get(("commands." + (char)('1' + leading))).charAt(1);
        for (int i = 0; i < val; i++) {
            if (captureStatus == 0) {
                owner = leading;
            }
            if (owner == leading && captureStatus == getStages().size() * 5) {
                break;
            }
            for (GamePlayer gp : offenders) {
                if (gp.getTeam() == leading) {
                    rewards.put(gp, rewards.getOrDefault(gp, 0.0) + 1.0 / leadval);
                }
            }
            double rem = 1.0;
            while (rem > 0) {
                int ot = 0;
                for (GamePlayer gp : rewards.keySet()) {
                    ot += gp.getTeam() != leading ? 1 : 0;
                }
                
                double zxc = rem / ot;
                for (Map.Entry<GamePlayer, Double> entry : rewards.entrySet()) {
                    if (entry.getKey().getTeam() != leading) {
                        double cur = Math.min(entry.getValue(), zxc);
                        entry.setValue(entry.getValue() - cur);
                        rem -= cur;
                    }
                }
                int was = rewards.size();
                rewards.entrySet().removeIf(e -> e.getValue() == 0);
                if (rewards.size() == was) {
                    break;
                }
            }
            
            if (owner == leading) {
                updateBlock(teamcode);
                captureStatus++;
                if (captureStatus == getStages().size() * 5) {
                    onCapture();
                    break;
                }
            } else {
                captureStatus--;
                updateBlock('f');
            }
        }
        String indText = Lang.get(("commands." + (char)('1' + owner))).substring(0, 2) + "||||||||||||||||||";
        int idx = captureStatus / 10;
        String capture = indText.substring(0, idx+2);
        if (idx != indText.length() - 2) {
            capture += "§f" + indText.substring(idx+2);
        }
        captureIndicator.setText(capture);
    }

    private void updateBlock(char teamcode) {
        if (captureStatus % 5 == 4) {
            Pair<Material, Vector> block = getStages().get(captureStatus / 5);
            Location wh = block.getValue().toLocation(location.world).add(location);
            GameUtils.changeColor(wh, teamcode);
            location.getWorld().playSound(wh, Sound.BLOCK_STONE_BREAK, 1.07f, 1);
            if (captureStatus % 30 == 29) {
                Location at = getChanging().get(captureStatus / 30).getValue().
                        toLocation(location.world).add(location);
                GameUtils.changeColor(at, teamcode);
            }
        }
    }

    public void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        crystal = new MovingCrystal(location.clone().add(0, 2, 0));
        crystal.setMaxHeight(maxHeight);
        crystal.spawn();

        captureIndicator = new FlyingText(location.clone().add(-0.5, 3.5, 0.5), "||||||||||||||||||");
        CollectorStructure.build(location);
        etherAdd = new SafeRunnable() {
            double progress = 0;
            @Override
            public void run () {
                progress += (double) captureStatus / 180 / etherDelay;
                if (progress >= 1.0) {
                    EtherCapture.getInstance().addEther(owner, etherMineValue);
                    progress--;
                }
                crystal.setProgress(progress);
            }
        };
        etherAdd.runTaskTimer(Plugin.getInstance(), 2, 1);
    }

    public void onCapture() {
        RewardManager manager = Game.getInstance().getRewardManager();
        
        double mult = (double) manager.get("collectorCaptured").getCount() / getStages().size() / 10;
        for (Map.Entry<GamePlayer, Double> entry : rewards.entrySet()) {
            double al = Game.getInstance().getMultiplier(entry.getKey());
            
            int cnt = (int) Math.ceil(entry.getValue() * mult * al);
            String name = entry.getKey().getPlayer().getName();
            Game.getInstance().getGameStatsManager().addCapturePoints(name, (int) (double) entry.getValue());
            Game.getInstance().getGameStatsManager().addExp(entry.getKey(), cnt);
            Game.getInstance().getRewardManager().addExp(name, cnt);
            MessagingUtils.sendSubtitle(Lang.get("rewmes.capture").
                    replace("%exp%", String.valueOf(cnt)), entry.getKey().getPlayer(), 0, 20, 0);
        }
        rewards.clear();
        
        Bukkit.getServer().broadcastMessage(Lang.get("game.collectorCaptureInform").
                replace("%num%", "" + (number + 1)).
                replace("%com%", Lang.get("commands." + (char)('1' + owner))));
        Game.getWorld().playSound(location, Sound.ENTITY_ENDERDRAGON_GROWL, 100, 1);
    }

    public void unload() {
        if (!loaded) {
            return;
        }
        loaded = false;
        crystal.die();
        captureIndicator.unload();
        etherAdd.cancel();
    }

    public boolean isInside(Location location) {
        return location.getWorld().equals(this.location.getWorld()) && location.distance(this.location) < 4;
    }
    
    public IntPosition getPosition() {
        return new IntPosition(location);
    }

    public void setPosition(IntPosition pos) {
        location = pos.toLocation(Game.getWorld()).add(0,-1,0);
    }
}
