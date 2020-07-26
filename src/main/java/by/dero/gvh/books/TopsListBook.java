package by.dero.gvh.books;

import by.dero.gvh.bookapi.BookButton;
import by.dero.gvh.bookapi.BookGUI;
import by.dero.gvh.bookapi.BookManager;
import by.dero.gvh.bookapi.BookUtil;
import by.dero.gvh.model.Lang;
import by.dero.gvh.stats.GamePlayerStats;
import by.dero.gvh.stats.GameStats;
import by.dero.gvh.stats.GameStatsUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class TopsListBook extends BookGUI {
    private final GameStats game;
    private final String considered;
    private Runnable backAction;

    public TopsListBook(BookManager manager, Player player, String considered, GameStats game) {
        super(manager, player);
        this.game = game;
        this.considered = considered;
    }

    @Override
    public void build() {
        BookUtil.PageBuilder builder = new BookUtil.PageBuilder()
                .add("§6§lТопы§8 - Игра #" + game.getId()).newLine();
        builder.add("§0Время §8» §0" + GameStatsUtils.getDurationString(game)).newLine();
        builder.add("§0Дата §8» §0" + GameStatsUtils.getDateString(game.getStartTime())).newLine();

        if (game.getPercentToWin() != null) {
            for (int team = 0; team < game.getPercentToWin().size(); ++team) {
                int percent = game.getPercentToWin().get(team);
                String value = percent >= 100 ? "§2Победа" : ("§9" + percent + '%');
                builder.add(Lang.get("commands." + (team + 1)) + " §8» " + value).newLine();
            }
        }
        builder.newLine();
        builder.add(BookUtil.TextBuilder.of("§3[Топ - Полезность]").onClick(new BookButton(this, () -> {
            PlayerTopBook book = new PlayerTopBook(getManager(), getPlayer(), considered, game,
                    "§6§lТоп - Полезность", GameStatsUtils.getAdvancementTop(game));
            book.setBackAction(this::open);
            book.build();
            book.open();
        })).build()).newLine();
        builder.add(BookUtil.TextBuilder.of("§c[Топ - Убийства]").onClick(new BookButton(this, () -> {
            PlayerTopBook book = new PlayerTopBook(getManager(), getPlayer(), considered, game,
                    "§6§lТоп - Убийства", GameStatsUtils.getKillTop(game));
            book.setBackAction(this::open);
            book.build();
            book.open();
        })).build()).newLine();
        builder.add(BookUtil.TextBuilder.of("§5[Топ - У/С/П]").onClick(new BookButton(this, () -> {
            PlayerTopBook book = new PlayerTopBook(getManager(), getPlayer(), considered, game,
                    "§6§lТоп - У/С/П", GameStatsUtils.getKDATop(game));
            book.setOnOnePage(5);
            book.setBackAction(this::open);
            book.build();
            book.open();
        })).build()).newLine();
        builder.add(BookUtil.TextBuilder.of("§4[Топ - Урон]").onClick(new BookButton(this, () -> {
            PlayerTopBook book = new PlayerTopBook(getManager(), getPlayer(), considered, game,
                    "§6§lТоп - Урон", GameStatsUtils.getDamageTop(game));
            book.setBackAction(this::open);
            book.build();
            book.open();
        })).build()).newLine();
        builder.add(BookUtil.TextBuilder.of("§9[Топ - Захват]").onClick(new BookButton(this, () -> {
            PlayerTopBook book = new PlayerTopBook(getManager(), getPlayer(), considered, game,
                    "§6§lТоп - Захват", GameStatsUtils.getCaptureTop(game));
            book.setBackAction(this::open);
            book.build();
            book.open();
        })).build()).newLine();

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
