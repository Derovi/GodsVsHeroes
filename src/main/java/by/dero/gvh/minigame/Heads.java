package by.dero.gvh.minigame;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Heads {
    TEAMBOOSTER("NDZlMDNlZTA3YTBkZDNlZWNmMDE2YmI3YzM4OWQ2Yjk0ZjJkZWVkYjkyOGQ1ZjA0NmFjOWZhNzFhZDQ1MjQ0ZSJ9fX0=", "teambooster"),
    SINGLEBOOSTER("NDRjMGRjZmU4YWFjZjZkZGVlMzczNWJlMWQwYjkxMzY1ZDRhZjNjNjUzZWMzZTg4NzU3ODFkYmIzMDgyY2NkIn19fQ==", "singlebooster"),
    GUNSMITH("YTZiOGQ4NzQ1ZjZmYzdhMGE3NzM1NGNlMWE5ZjMwNDY4MTdmNjZkMmQzYWZkMWJjZGFjNmQyZDEwZjM3OSJ9fX0=", "gunsmith"),
    ASSASSIN("Mzk1NzEyYjZlMWIzOGY5MmUyMWE1MmZiNzlhZjUzM2I3M2JiNWRkNWNiZGFmOTJlZTY0YjkzYWFhN2M0NjRkIn19fQ==", "assassin"),
//    DOVAHKIIN("ZjdiMWI5MTI1MjFmMTMwZDNiZThiNWEwOTY1NDRmM2E3NTM1N2M1ZTE5MzFhZWJkZWU5MmJmNWE1Y2RjYTE3NCJ9fX0=", "dovahkiin"),
    HORSEMAN("ODdiNTA0MDBiZjVlNDZlZjYwMDA5NzU2NWI0NDQwYjg0Y2NjMmIxYTc1NTA0MjJhZjdjODczYzJiODg2ZDlhYyJ9fX0=", "horseman"),
    LUCIFER("N2ZkNWQzMjI3M2NjYjNkZjk3NDg1MGNmYTRjNWExMGY5YTQ0ZmY5YjY2ZWM3MGQ5ZWFjODI4ZGNhZDUifX19", "lucifer"),
    PALADIN("OGU3MDM0OTRkMjY2ZDVjYjgzZjFhZDNmMWM0NjdmY2JmNDQyZTkxM2RiZjRkMzBlOGFjZGUyYWUwMTU5ODg2NyJ9fX0=", "paladin"),
    THOR("MmE5ZjgzMzI5YTJlNDc1YTc1MzM1YjM5NDlhYTRkMDU0ZjlkZTQxM2JmYjI4YWE2MGRlMmU1MjU5ZWNhYWQxIn19fQ==", "thor"),
    WARRIOR("YTJkYzEzMzcwNjM3NGNiZjg0MTg3ZjUxZDk1NDYyMmY0NjNmNGZkODNhMzQ3ZjMyZWYwZTkzMDVlMDkwNWVhMSJ9fX0=", "warrior"),
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
        switch (name) {
            case "dovahkiin": return new ItemStack(Material.SKULL_ITEM, 1, (byte) 5);
        }
        
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
