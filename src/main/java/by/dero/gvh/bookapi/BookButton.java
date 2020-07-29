package by.dero.gvh.bookapi;

import net.md_5.bungee.api.chat.ClickEvent;

import java.util.UUID;

public class BookButton implements BookUtil.ClickAction {
    public Runnable onClick;
    public UUID uuid;

    public BookButton(BookGUI gui, Runnable onClick) {
        this.onClick = onClick;
        uuid = UUID.randomUUID();
        gui.getButtons().put(uuid, this);
    }

    @Override
    public ClickEvent.Action action() {
        return ClickEvent.Action.RUN_COMMAND;
    }

    @Override
    public String value() {
        return "bookapi " + uuid;
    }
}
