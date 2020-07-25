package by.dero.gvh.books;

import by.dero.gvh.bookapi.BookButton;
import by.dero.gvh.bookapi.BookGUI;
import by.dero.gvh.bookapi.BookManager;
import by.dero.gvh.bookapi.BookUtil;
import by.dero.gvh.model.Lang;
import by.dero.gvh.stats.GamePlayerStats;
import by.dero.gvh.stats.GameStats;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class GameStatsBook extends BookGUI {
    private final GameStats game;
    private final Player considered;
    @Getter
    @Setter
    private Runnable backAction = null;

    public GameStatsBook(BookManager manager, Player player, Player considered, GameStats game) {
        super(manager, player);
        this.game = game;
        this.considered = considered;
    }

    @Override
    public void build() {
        GamePlayerStats playerStats = game.getPlayers().get(considered.getName());
        String winStatus = "§a§lПобеда";
        if (playerStats.getTeam() != game.getWonTeam()) {
            winStatus = "§c§lПоражение";
        }
        BookUtil.PageBuilder builder = new BookUtil.PageBuilder()
                .add(winStatus).add("§8 - Игра #" + game.getId()).newLine().newLine();
        if (considered.getUniqueId().equals(getPlayer().getUniqueId())) {
            builder.add("§6Ваша статистика:");
        } else {
            builder.add("§6Статистика §8»§9 " + considered.getName() + "§6:");
        }
        builder.newLine();
        builder.add("§0Класс §8» §5" + Lang.get("classes." + playerStats.getClassName())).newLine();
        builder.add("§0Убийств §8» §a" + playerStats.getKills()).newLine();
        builder.add("§0Смертей §8» §c" + playerStats.getDeaths()).newLine();
        builder.add("§0Помощей §8» §6" + playerStats.getAssists()).newLine();
        builder.add("§0Захват §8» §9" + playerStats.getCapturePoints()).newLine();
        builder.add("§0Урон §8» §4" + playerStats.getDamageDealt()).newLine();
        builder.add("§0Опыт §8» §2" + playerStats.getExpGained()).newLine();

        builder.newLine();
        builder.add(BookUtil.TextBuilder.of("§3§l[Топы]").onClick(new BookButton(this, () -> {
            TopsListBook book = new TopsListBook(getManager(), getPlayer(), considered, game);
            book.setBackAction(() -> open());
            book.build();
            book.open();
        })).build());
        if (backAction != null) {
            builder.newLine();
            builder.add(BookUtil.TextBuilder.of("§5§l[Назад]").onClick(new BookButton(this, () -> {
                backAction.run();
            })).build());
        }

        setBook(BookUtil.writtenBook()
                .author("§6derovi")
                .title("stats")
                .pages(builder.build()
                ).build());
    }
}
