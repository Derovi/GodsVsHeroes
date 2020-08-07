package by.dero.gvh.books;

import by.dero.gvh.Plugin;
import by.dero.gvh.bookapi.BookButton;
import by.dero.gvh.bookapi.BookGUI;
import by.dero.gvh.bookapi.BookManager;
import by.dero.gvh.bookapi.BookUtil;
import by.dero.gvh.lobby.Lobby;
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
        BookUtil.PageBuilder builder = new BookUtil.PageBuilder().add("§6§lСтатистика §8§l»").newLine();
        String nameString = MessagingUtils.getPrefixAddition(Bukkit.getOfflinePlayer(considered)) + considered;
        StringBuilder stringBuilder = new StringBuilder();
        for (int idx = 0; idx < nameString.length(); ++idx) {
            stringBuilder.append(nameString.charAt(idx));
            if (nameString.charAt(idx) == '§') {
                ++idx;
                stringBuilder.append(nameString.charAt(idx)).append("§l");
            }
        }
        builder.add(stringBuilder.toString());
        builder.newLine().newLine();
        builder.add("§2Побед §8» §2§l" + stats.getWins()).newLine();
        builder.add("§cПоражений §8» §c§l" + (stats.getLooses() + stats.getAbandoned())).newLine();
        builder.add("§8(Покинуто » §4§l" + stats.getAbandoned() + "§8)").newLine();
        builder.add("§9Уровень §8» §9§l" + stats.getLevel().getLevel() + "★").newLine();
        builder.add("§6Топ §8» §6§l" +
                Lobby.getInstance().getTopManager().getPlayerOrder(considered) + " §6место").newLine();

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
