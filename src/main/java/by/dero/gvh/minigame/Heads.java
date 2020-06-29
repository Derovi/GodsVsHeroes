package by.dero.gvh.minigame;

import org.bukkit.inventory.ItemStack;

public enum Heads {
    YOUTUBE("ZDJmNmMwN2EzMjZkZWY5ODRlNzJmNzcyZWQ2NDU0NDlmNWVjOTZjNmNhMjU2NDk5YjVkMmI4NGE4ZGNlIn19fQ==","youtube"),
    CAT("Njk3NDljYTk1MjM4YjVhZjkzOTA3ZjkyNGYxZDU5MTVkZjc3MzFlZTgzYzE1YWM3NzZjYTgzMmM0M2U0MDYyIn19fQ==","cat"),
    HEAL("ZjdjN2RmNTJiNWU1MGJhZGI2MWZlZDcyMTJkOTc5ZTYzZmU5NGYxYmRlMDJiMjk2OGM2YjE1NmE3NzAxMjZjIn19fQ==", "heal"),
    RESISTANCE("MjUyNTU5ZjJiY2VhZDk4M2Y0YjY1NjFjMmI1ZjJiNTg4ZjBkNjExNmQ0NDY2NmNlZmYxMjAyMDc5ZDI3Y2E3NCJ9fX0=", "resistance"),
    SPEED("M2Q2MWMzOGE1MmVjYjAxMjk5ZTFkNmZmMjM0NTFkYmMzMjE0NjdhZDJiZGM3YzZiMjU2ZGVkNzE1ZTVjMWQifX19", "speed");

    private ItemStack item;
    private String idTag;
    private String prefix = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv";
    Heads(String texture, String id)
    {
        item = LootsManager.createSkull(prefix + texture, id);
        idTag = id;
    }

    public ItemStack getItemStack()
    {
        return item;
    }

    public String getName()
    {
        return idTag;
    }

}
