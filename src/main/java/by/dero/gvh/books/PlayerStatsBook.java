package by.dero.gvh.books;

import by.dero.gvh.Plugin;
import by.dero.gvh.bookapi.BookButton;
import by.dero.gvh.bookapi.BookGUI;
import by.dero.gvh.bookapi.BookManager;
import by.dero.gvh.bookapi.BookUtil;
import by.dero.gvh.model.Lang;
import by.dero.gvh.stats.GamePlayerStats;
import by.dero.gvh.stats.GameStats;
import by.dero.gvh.stats.PlayerStats;
import by.dero.gvh.utils.MessagingUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerStatsBook extends BookGUI {
    private final PlayerStats stats;
    private final String considered;
    @Getter
    @Setter
    private Runnable backAction = null;

    public PlayerStatsBook(BookManager manager, Player player, String considered) {
        super(manager, player);
        this.considered = considered;
        this.stats = Plugin.getInstance().getGameStatsData().getPlayerStats(considered);
    }

    @Override
    public void build() {
        BookUtil.PageBuilder builder = new BookUtil.PageBuilder();
        builder.add(MessagingUtils.getPrefixAddition(Bukkit.getOfflinePlayer(considered)) + considered);
        builder.newLine().newLine();
        builder.add("§aПобед §8» §a§l" + stats.getWins()).newLine();
        builder.add("§cПоражений §8» §a§l" + (stats.getLooses() + stats.getAbandoned())).newLine();
        builder.add("§8(Покинуто » §4§l" + stats.getAbandoned() + "§8)").newLine();

        if (!stats.getGames().isEmpty()) {
            builder.newLine();
            builder.add(BookUtil.TextBuilder.of("§3§l[Последняя игра]").onClick(new BookButton(this, () -> {
                GameStatsBook gameStatsBook = new GameStatsBook(getManager(),
                        getPlayer(), considered, Plugin.getInstance().getGameStatsData().getGameStats(
                                stats.getGames().get(stats.getGames().size() - 1)));
                gameStatsBook.setBackAction(this::open);
                gameStatsBook.build();
                gameStatsBook.open();
            })).build());
        }

        if (backAction != null) {
            builder.newLine();
            builder.add(BookUtil.TextBuilder.of("§5§l[Назад]").onClick(new BookButton(this, () -> {
                backAction.run();
            })).build());
        } else {
            builder.newLine();
            builder.add(BookUtil.TextBuilder.of("§5§l[Закрыть]").onClick(new BookButton(this, () -> {})).build());
        }

        setBook(BookUtil.writtenBook()
                .author("§6derovi")
                .title("stats")
                .pages(builder.build()
                ).build());
    }
}
