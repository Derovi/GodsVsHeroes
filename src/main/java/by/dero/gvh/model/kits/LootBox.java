package by.dero.gvh.model.kits;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootBox {
    private String name;
    private String owner;
    private int cost;

    public LootBox(String name, String owner, int cost) {
        this.name = name;
        this.owner = owner;
        this.cost = cost;
    }

    @Getter
    @Setter
    private List<LootBoxItem> elements = new ArrayList<>();

    public void add(LootBoxItem entry) {
        elements.add(entry);
    }

    public LootBoxItem getRandomItem() {
        int summaryChance = 0;
        for (LootBoxItem lootBoxItem : elements) {
            summaryChance += lootBoxItem.getChance();
        }
        int number = new Random().nextInt(summaryChance);
        for (LootBoxItem lootBoxItem : elements) {
            summaryChance -= lootBoxItem.getChance();
            if (number >= summaryChance) {
                return lootBoxItem;
            }
        }
        return elements.get(0);
    }
}
