package by.dero.gvh.minigame;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.Lang;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ru.cristalix.core.chat.ChatContext;
import ru.cristalix.core.chat.ChatTextComponent;
import ru.cristalix.core.chat.IChatService;
import ru.cristalix.core.chat.IConstantChatView;
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


        new BukkitRunnable() {
            @Override
            public void run() {
                loadTabComponents();
                reloadTab();
            }
        }.runTaskTimer(plugin, 20, 20);
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
        for (GamePlayer gp : Minigame.getInstance().getGame().getPlayers().values()) {
            addTab(new TabTextComponent(gp.getTeam(), TextFormat.NONE,
                    (uuid) -> uuid.equals(gp.getPlayer().getUniqueId()), uuid -> CompletableFuture.supplyAsync(() -> {
                return new BaseComponent[] {new TextComponent(Lang.get("teamTabPrefix." + (gp.getTeam() + 1)))};
            })));
        }
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