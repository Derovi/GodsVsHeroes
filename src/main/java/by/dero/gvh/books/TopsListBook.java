package by.dero.gvh.books;

import by.dero.gvh.bookapi.BookButton;
import by.dero.gvh.bookapi.BookGUI;
import by.dero.gvh.bookapi.BookManager;
import by.dero.gvh.bookapi.BookUtil;
import by.dero.gvh.model.Lang;
import by.dero.gvh.stats.GamePlayerStats;
import by.dero.gvh.stats.GameStats;
import by.dero.gvh.stats.GameStatsUtils;
import org.bukkit.entity.Player;

public class TopsListBook extends BookGUI {
    private GameStats game;
    private Player considered;

    public TopsListBook(BookManager manager, Player player, Player considered, GameStats game) {
        super(manager, player);
        this.game = game;
        this.considered = considered;
    }

    @Override
    public void build() {
        GamePlayerStats playerStats = game.getPlayers().get(considered.getName());
        BookUtil.PageBuilder builder = new BookUtil.PageBuilder()
                .add("§6§lТопы§8 - Игра #" + game.getId()).newLine();
        builder.add("§8Длительность » ").newLine();
        builder.add("§8Дата » " + GameStatsUtils.getDateString(game.getStartTime())).newLine().newLine();

        if (game.getPercentToWin() != null) {

        }

        builder.newLine();
        builder.add("§3[Топ - Полезность]").newLine();
        builder.add("§c[Топ - Убийства]").newLine();
        builder.add("§5[Топ - У/С/П]").newLine();
        builder.add("§4[Топ - Урон]").newLine();
        builder.add("§9[Топ - Захват]").newLine().newLine();

        builder.add("§5§l[Назад]");

        builder.newLine();
        builder.add(BookUtil.TextBuilder.of("§5§l[Назад]").onClick(new BookButton(this, new Runnable() {
            @Override
            public void run() {

            }
        })).build());

        setBook(BookUtil.writtenBook()
                .author("§6derovi")
                .title("stats")
                .pages(builder.build()
                ).build());
    }
}
