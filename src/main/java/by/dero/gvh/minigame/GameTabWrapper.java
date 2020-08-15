package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.GameUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.cristalix.core.chat.ChatTextComponent;
import ru.cristalix.core.tab.IConstantTabView;
import ru.cristalix.core.tab.ITabService;
import ru.cristalix.core.tab.TabTextComponent;
import ru.cristalix.core.text.TextFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GameTabWrapper {

    private List<ChatTextComponent> prefixes = new ArrayList<>(), suffixes = new ArrayList<>();

    private List<TabTextComponent> tabTextComponents = new ArrayList<>();

    public GameTabWrapper(Plugin plugin) {
        loadTabComponents();
        new BukkitRunnable() {
            @Override
            public void run() {
                reloadTab();
            }
        }.runTaskTimer(Plugin.getInstance(), 5, 5);
        reloadTab();
    }

    public void addChatPrefix(ChatTextComponent component) {
        prefixes.add(component);
    }

    public void addChatSuffix(ChatTextComponent component) {
        suffixes.add(component);
    }

    public void addTab(TabTextComponent component) {
        tabTextComponents.add(component);
        reloadTab();
    }

    public void loadTabComponents() {
        tabTextComponents.clear();
        addTab(new TabTextComponent(0, TextFormat.NONE, uuid -> true, uuid -> CompletableFuture.supplyAsync(() -> {
            GamePlayer gp = Minigame.getInstance().getGame().getPlayers().getOrDefault(
                    Bukkit.getPlayer(uuid).getName(), null);
            if (gp == null) {
                return new BaseComponent[] {new TextComponent()};
            }
            return new BaseComponent[] {new TextComponent(Lang.get("teamTabPrefix." + (gp.getTeam() + 1)) + " §7" +
                    gp.getPlayerStats().getLevel().getLevel() + "★")};
        }), uuid -> CompletableFuture.supplyAsync(() -> {
            GamePlayer gp = Minigame.getInstance().getGame().getPlayers().getOrDefault(
                    Bukkit.getPlayer(uuid).getName(), null);
            if (gp == null || gp.getTeam() == -1) {
                return 100;
            }
            return gp.getTeam();
        })));
    }

    public void reloadTab() {
        //System.out.println("reload");
        IConstantTabView tabView = ITabService.get().createConstantTabView();
        tabTextComponents.forEach(tabView::addPrefix);
        ITabService.get().setDefaultTabView(tabView);
        for (Player player : Bukkit.getOnlinePlayers()) {
            ITabService.get().update(player);
        }
    }

}