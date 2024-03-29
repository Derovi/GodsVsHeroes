package by.dero.gvh.books;

import by.dero.gvh.bookapi.BookButton;
import by.dero.gvh.bookapi.BookGUI;
import by.dero.gvh.bookapi.BookManager;
import by.dero.gvh.bookapi.BookUtil;
import by.dero.gvh.model.Lang;
import by.dero.gvh.stats.GameStats;
import by.dero.gvh.stats.GameStatsUtils;
import by.dero.gvh.stats.TopEntry;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class PlayerTopBook extends BookGUI {
    private final String considered;
    private final GameStats game;
    private final String title;
    private final List<TopEntry> top;
    @Getter @Setter
    private int onOnePage = 8;


    @Getter @Setter
    private Runnable backAction;

    public PlayerTopBook(BookManager manager, Player player, String considered,
                         GameStats game, String title, List<TopEntry> top) {
        super(manager, player);
        this.considered = considered;
        this.game = game;
        this.title = title;
        this.top = top;
    }

    @Override
    public void build() {
        List<BaseComponent[]> pages = new ArrayList<>();

        for (int first = 0; first < game.getPlayers().size(); first += onOnePage) {
            BookUtil.PageBuilder builder = new BookUtil.PageBuilder()
                    .add(title).newLine().newLine();

            for (int idx = first; idx < Math.min(first + onOnePage, top.size()); ++idx) {
                TopEntry entry = top.get(idx);
                String nameColor = "§0";
                if (entry.getName().equals(considered)) {
                    nameColor = "§5";
                }
                builder.add(entry.getOrder() + ". ").add(
                        BookUtil.TextBuilder.of(nameColor + entry.getName()).onClick(new BookButton(this, () -> {
                            GameStatsBook gameStatsBook = new GameStatsBook(getManager(),
                                    getPlayer(), entry.getName(), game);
                            gameStatsBook.setBackAction(this::open);
                            gameStatsBook.build();
                            gameStatsBook.open();
                        })).build()
                ).add(" §8» §3" + entry.getValue()).newLine();
            }

            builder.newLine();
            builder.add(BookUtil.TextBuilder.of("§5§l[Назад]").onClick(new BookButton(this, () -> {
                if (backAction != null) {
                    backAction.run();
                }
            })).build());
            pages.add(builder.build());
        }

        setBook(BookUtil.writtenBook()
                .author("§6derovi")
                .title("stats")
                .pages(pages).build());
    }
}
