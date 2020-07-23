package by.dero.gvh.minigame;

import org.bukkit.inventory.ItemStack;

public enum Heads {
    ASSASSIN("NzdmN2I1MWNhNjk2NmJlMmRlY2ZlYjNkNTg1ZDJkNjU0MTkzYjAzY2I4YzEzNjU4YTQzNjliNTkxNGRmZTg2YyJ9fX0=", "assassin"),
    DOVAHKIIN("ZjdiMWI5MTI1MjFmMTMwZDNiZThiNWEwOTY1NDRmM2E3NTM1N2M1ZTE5MzFhZWJkZWU5MmJmNWE1Y2RjYTE3NCJ9fX0=", "dovahkiin"),
    HORSEMAN("OTVlNGZkZmI5NjE3MzFjYmI3MTMxMWE2NzQzNDRhZGUzOWQ5MTFkM2M4ZGI4MzhkYTFjMDk5ODFhYmMzMDc1OCJ9fX0=", "horseman"),
    LUCIFER("N2ZkNWQzMjI3M2NjYjNkZjk3NDg1MGNmYTRjNWExMGY5YTQ0ZmY5YjY2ZWM3MGQ5ZWFjODI4ZGNhZDUifX19", "lucifer"),
    PALADIN("NzFiMzYxM2RmNTBlZmY2MGIxNjM0MDU3YzJlZjc2MWQzNTlkMmEwYTJhODEyMDM4NTczYTMyNjc3NmUwYzMxYSJ9fX0=", "paladin"),
    THOR("MmE5ZjgzMzI5YTJlNDc1YTc1MzM1YjM5NDlhYTRkMDU0ZjlkZTQxM2JmYjI4YWE2MGRlMmU1MjU5ZWNhYWQxIn19fQ==", "thor"),
    WARRIOR("NDhhNWMzMzRkNzk0OGFkOWE2ZTg1ZTNkNDdhMTAzZTg3N2VmMDk3OGQ4Y2Q5M2VjMGVkYWY0OWNjNTgxYjc0In19fQ==", "warrior"),
    YOUTUBE("ZDJmNmMwN2EzMjZkZWY5ODRlNzJmNzcyZWQ2NDU0NDlmNWVjOTZjNmNhMjU2NDk5YjVkMmI4NGE4ZGNlIn19fQ==","youtube"),
    CAT("Njk3NDljYTk1MjM4YjVhZjkzOTA3ZjkyNGYxZDU5MTVkZjc3MzFlZTgzYzE1YWM3NzZjYTgzMmM0M2U0MDYyIn19fQ==","cat"),
    HEAL("ZjdjN2RmNTJiNWU1MGJhZGI2MWZlZDcyMTJkOTc5ZTYzZmU5NGYxYmRlMDJiMjk2OGM2YjE1NmE3NzAxMjZjIn19fQ==", "heal"),
    RESISTANCE("MjUyNTU5ZjJiY2VhZDk4M2Y0YjY1NjFjMmI1ZjJiNTg4ZjBkNjExNmQ0NDY2NmNlZmYxMjAyMDc5ZDI3Y2E3NCJ9fX0=", "resistance"),
    SPEED("M2Q2MWMzOGE1MmVjYjAxMjk5ZTFkNmZmMjM0NTFkYmMzMjE0NjdhZDJiZGM3YzZiMjU2ZGVkNzE1ZTVjMWQifX19", "speed");

    private ItemStack item;
    private String idTag;
    private final String prefix = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv";
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
    
    public static ItemStack getHead(String name)
    {
        for (Heads head : Heads.values())
        {
            if (head.getName().equalsIgnoreCase(name))
            {
                return head.getItemStack();
            }
        }
        return null;
    }
}
