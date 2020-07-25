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
    private final GameStats game;
    private final Player considered;
    private Runnable backAction;

    public TopsListBook(BookManager manager, Player player, Player considered, GameStats game) {
        super(manager, player);
        this.game = game;
        this.considered = considered;
    }

    @Override
    public void build() {
        BookUtil.PageBuilder builder = new BookUtil.PageBuilder()
                .add("§6§lТопы§8 - Игра #" + game.getId()).newLine();
        builder.add("§8Длительность » ").newLine();
        builder.add("§8Дата » " + GameStatsUtils.getDateString(game.getStartTime())).newLine().newLine();

        if (game.getPercentToWin() != null) {
            for (int team = 0; team < game.getPercentToWin().size(); ++team) {
                int percent = game.getPercentToWin().get(team);
                String value = percent >= 100 ? "§aПобеда" : ("§c" + percent + '%');
                builder.add(Lang.get("commands." + (percent + 1)) + " §8» " + value).newLine();
            }
        }
        builder.newLine().newLine();
        builder.add("§3[Топ - Полезность]").newLine();
        builder.add("§c[Топ - Убийства]").newLine();
        builder.add("§5[Топ - У/С/П]").newLine();
        builder.add("§4[Топ - Урон]").newLine();
        builder.add("§9[Топ - Захват]").newLine().newLine();

        builder.newLine();
        builder.add(BookUtil.TextBuilder.of("§5§l[Назад]").onClick(new BookButton(this, () -> {
            if (backAction != null) {
                backAction.run();
            }
        })).build());

        setBook(BookUtil.writtenBook()
                .author("§6derovi")
                .title("stats")
                .pages(builder.build()
                ).build());
    }

    public Runnable getBackAction() {
        return backAction;
    }

    public TopsListBook setBackAction(Runnable backAction) {
        this.backAction = backAction;
        return this;
    }
}
