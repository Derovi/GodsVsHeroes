package by.dero.gvh.lobby;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Lang;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.PlayerLevel;
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

public class LobbyTabWrapper {

    private List<ChatTextComponent> prefixes = new ArrayList<>(), suffixes = new ArrayList<>();

    private List<TabTextComponent> tabTextComponents = new ArrayList<>();

    public LobbyTabWrapper(Plugin plugin) {
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
            PlayerLobby playerLobby = Lobby.getInstance().getActiveLobbies().getOrDefault(
                    Bukkit.getPlayer(uuid).getName(), null);
            if (playerLobby == null) {
                return new BaseComponent[] {new TextComponent()};
            }
            PlayerLevel level = new PlayerLevel(playerLobby.getStats().getExp());
            return new BaseComponent[] {new TextComponent("§b" + level.getLevel() + "★ ")};
        }), uuid -> CompletableFuture.supplyAsync(() -> {
            PlayerLobby playerLobby = Lobby.getInstance().getActiveLobbies().getOrDefault(
                    Bukkit.getPlayer(uuid).getName(), null);
            if (playerLobby == null) {
                return 100000;
            }
            return 100000 - new PlayerLevel(playerLobby.getStats().getExp()).getLevel();
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