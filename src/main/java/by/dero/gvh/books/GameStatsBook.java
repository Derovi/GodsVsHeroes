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
    private final String considered;
    @Getter
    @Setter
    private Runnable backAction = null;

    public GameStatsBook(BookManager manager, Player player, String considered, GameStats game) {
        super(manager, player);
        this.game = game;
        this.considered = considered;
    }

    @Override
    public void build() {
        BookUtil.PageBuilder builder = new BookUtil.PageBuilder();
        if (game.getDeserters().contains(considered)) {
            builder.add("§4§lПокинута").newLine().add("§8Игра #" + game.getId()).newLine().newLine();
            builder.add("§cТебе §4§lне§c выдан опыт.").newLine();
            builder.add("§cТебе засчитано §4§lпоражение§c.").newLine();
            builder.add("§cТы §4§lподставил §cкоманду.").newLine();
            builder.newLine();
            builder.add("§6§lИграй до конца!!").newLine();
        } else {
            GamePlayerStats playerStats = game.getPlayers().get(considered);
            String winStatus = "§a§lПобеда";
            if (playerStats.getTeam() != game.getWonTeam()) {
                winStatus = "§c§lПоражение";
            }
            builder.add(winStatus).newLine().add("§8Игра #" + game.getId()).newLine().newLine();
            if (considered.equals(getPlayer().getName())) {
                builder.add("§6Ваша статистика:");
            } else {
                builder.add("§6Игрок §8»§9 " + considered + "§6:");
            }
            builder.newLine();
            builder.add("§0Класс §8» §5" + Lang.get("classes." + playerStats.getClassName())).newLine();
            builder.add("§0У/С/П §8» §a" + playerStats.getKills() +
                    "§8/§c" + playerStats.getDeaths() + "§8/§6" + playerStats.getAssists()).newLine();
            builder.add("§0Захват §8» §9" + playerStats.getCapturePoints()).newLine();
            builder.add("§0Урон §8» §4" + playerStats.getDamageDealt()).newLine();
            builder.add("§0Опыт §8» §2" + playerStats.getExpGained()).newLine();
        }

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
