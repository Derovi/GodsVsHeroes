package by.dero.gvh.books;

import by.dero.gvh.bookapi.BookGUI;
import by.dero.gvh.bookapi.BookManager;
import by.dero.gvh.bookapi.BookUtil;
import by.dero.gvh.stats.GamePlayerStats;
import by.dero.gvh.stats.GameStats;
import org.bukkit.entity.Player;

public class GameStatsBook extends BookGUI {
    private GameStats game;
    private Player considered;

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
                .add(winStatus).add("§8 - Игра #" + game.getId()).newLine();
        if (considered.getUniqueId().equals(getPlayer().getUniqueId())) {
            builder.add("§6Ваша статистика");
        } else {
            builder.add("§6Статистика §8»§9 " + considered.getName());
        }
        builder.add("§0Класс §8>> §b" + playerStats.getClassName());
        builder.add("§0Убийств §8>> §a" + playerStats.getKills());
        builder.add("§0Смертей §8>> §c" + playerStats.getDeaths());
        builder.add("§0Помощей §8>> §6" + playerStats.getAssists());
        builder.add("§0Захват §8>> §9" + playerStats.getCapturePoints());
        builder.add("§0Урон §8>> §4" + playerStats.getDamageDealt());
        builder.add("§0Опыт §8>> §2" + playerStats.getExpGained());
    }
}
