package by.dero.gvh.lobby.interfaces;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Heads;
import by.dero.gvh.model.*;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.InterfaceUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CosmeticInterface extends Interface {
    private final String className;
    private Runnable onBackButton = null;
    private List<CosmeticButton> cosmeticButtons = new ArrayList<>();

    public CosmeticInterface(InterfaceManager manager, Player player, String className) {
        super(manager, player, 6, Lang.get("cosmetic.titleHero").replace("%hero%",
                Lang.get("classes." + className)));
        this.className = className;
    }

    public void update() {
        clear();
        ItemStack empty = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 8);
        InterfaceUtils.changeName(empty, Lang.get("interfaces.empty"));
        for (int x = 0; x < 9; ++x) {
            for (int y = 0; y < 6; ++y) {
                addItem(x, y, empty);
            }
        }
        ItemStack backItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
        InterfaceUtils.changeName(backItem, Lang.get("interfaces.back"));
        addButton(0, 5, backItem, () -> {
            if (onBackButton != null) {
                onBackButton.run();
            }
        });
        addButton(1, 5, backItem, () -> {
            if (onBackButton != null) {
                onBackButton.run();
            }
        });
        addButton(0, 4, backItem, () -> {
            if (onBackButton != null) {
                onBackButton.run();
            }
        });
        ItemStack head;
        if (className.equals("all")) {
            head = GameUtils.getHead(getPlayer());
        } else {
            head = Heads.getHead(className);
        }
        addItem(4, 5, head);
        PlayerInfo playerInfo = Plugin.getInstance().getPlayerData().getPlayerInfo(getPlayer().getName());
        Map<String, Cosmetic> customizations = playerInfo.getCosmetics();
        for (CosmeticButton button : cosmeticButtons) {
            String name = button.getCosmeticName();
            CosmeticInfo info = Plugin.getInstance().getCustomizationManager().getCustomizations().get(name);
            ItemStack item = info.getItemStack();
            ItemStack subItem;
            int state = 0;
            if (customizations.containsKey(name)) {
                state = 1;
                if (customizations.get(name).isEnabled()) {
                    state = 2;
                }
            }
            Runnable runnable;
            if (state == 0) {
                subItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 3);
                runnable = () -> {
                    playerInfo.unlockCosmetic(name);
                    playerInfo.enableCosmetic(name);
                    Plugin.getInstance().getPlayerData().savePlayerInfo(getPlayer().getName(), playerInfo);
                    update();
                };
            } else if (state == 1) {
                subItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7);
                runnable = () -> {
                    playerInfo.enableCosmetic(name);
                    Plugin.getInstance().getPlayerData().savePlayerInfo(getPlayer().getName(), playerInfo);
                    update();
                };
            } else {
                subItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
                runnable = () -> {
                    playerInfo.disableCosmetic(name);
                    Plugin.getInstance().getPlayerData().savePlayerInfo(getPlayer().getName(), playerInfo);
                    update();
                };
            }
            addButton(button.getX(), button.getY(), item, runnable);
            addButton(button.getX(), button.getY() - 1, subItem, runnable);
        }
    }

    @AllArgsConstructor
    public static class CosmeticButton {
        @Getter
        @Setter
        private int x;

        @Getter
        @Setter
        private int y;

        @Getter
        @Setter
        private String cosmeticName;
    }

    public void registerCosmetic(CosmeticButton button) {
        cosmeticButtons.add(button);
    }

    public Runnable getOnBackButton() {
        return onBackButton;
    }

    public CosmeticInterface setOnBackButton(Runnable onBackButton) {
        this.onBackButton = onBackButton;
        return this;
    }
}
