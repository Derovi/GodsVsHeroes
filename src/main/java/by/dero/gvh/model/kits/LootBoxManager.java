package by.dero.gvh.model.kits;

import lombok.Getter;

import java.util.HashMap;

public class LootBoxManager {
    interface DonateKitBuilder {
        LootBox build(String playerName, int cost);
    }

    @Getter
    private final HashMap<String, DonateKitBuilder> donateKitBuilders = new HashMap<>();

    public LootBoxManager() {
        donateKitBuilders.put("expBox1", (playerName, cost) -> {
            LootBox lootBox = new LootBox("expBox1", playerName, cost);
            lootBox.add(new ExpLoot(playerName, 100, ExpLoot.Type.K1));
            lootBox.add(new ExpLoot(playerName, 80, ExpLoot.Type.K2));
            lootBox.add(new ExpLoot(playerName, 60, ExpLoot.Type.K3));
            lootBox.add(new ExpLoot(playerName, 40, ExpLoot.Type.K4));
            lootBox.add(new ExpLoot(playerName, 20, ExpLoot.Type.K5));
            lootBox.add(new ExpLoot(playerName, 10, ExpLoot.Type.K6));
            return lootBox;
        });
    }

    public LootBox buildBox(String playerName, String boxName, int cost) {
        return donateKitBuilders.get(boxName).build(playerName, cost);
    }
}
