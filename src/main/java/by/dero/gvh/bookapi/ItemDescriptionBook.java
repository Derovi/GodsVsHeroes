package by.dero.gvh.bookapi;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.ItemInfo;
import by.dero.gvh.model.Lang;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemDescriptionBook extends BookGUI {
    private final String className;
    private final String itemName;
    private Runnable backAction = null;

    public ItemDescriptionBook(BookManager manager, Player player, String className, String itemName) {
        super(manager, player);
        this.className = className;
        this.itemName = itemName;
    }

    @Override
    public void build() {
        BookUtil.PageBuilder builder = new BookUtil.PageBuilder()
                .add(BookUtil.TextBuilder.of("§6§l" + Lang.get("items." + itemName)).build())
                /*.newLine().newLine()
                .add(Lang.get("desc." + itemName))*/;

       /* ItemInfo info = Plugin.getInstance().getData().getItems().get(itemName).getLevels().get(
                Plugin.getInstance().getPlayerData().getPlayerInfo(getPlayer().getName()).getItemLevel(className, itemName));
        if (info.getLore() != null) {
            for (String line : info.getLore()) {
                if (line == null) {
                    continue;
                }
                builder.newLine().add(info.parseString(line).replace("§f", "§9"));
            }
        }

        builder.newLine();

        if (Plugin.getInstance().getPlayerData().getPlayerInfo(getPlayer().getName()).isClassUnlocked(className)) {
            builder.newLine().add(BookUtil.TextBuilder.of("§b[УЛУЧШИТЬ]").onClick(new BookButton(this, () -> {
                // TODO improve
            })).build());
        }
*/
        builder.newLine().add(BookUtil.TextBuilder.of("§5[НАЗАД]").onClick(new BookButton(this, () -> {
            if (backAction != null) {
                backAction.run();
            }
        })).build());

        setBook(BookUtil.writtenBook()
                .author("§6derovi")
                .title(itemName)
                .pages(builder.build()
                ).build());
    }

    public Runnable getBackAction() {
        return backAction;
    }

    public void setBackAction(Runnable backAction) {
        this.backAction = backAction;
    }
}
