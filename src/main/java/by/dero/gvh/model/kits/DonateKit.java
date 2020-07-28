package by.dero.gvh.model.kits;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class DonateKit {
    private String name;
    private String owner;
    private int cost;

    public DonateKit(String name, String owner, int cost) {
        this.name = name;
        this.owner = owner;
        this.cost = cost;
    }

    @Getter
    @Setter
    private List<KitEntry> elements = new ArrayList<>();

    public void add(KitEntry entry) {
        elements.add(entry);
    }

    public void give() {
        for (KitEntry entry : elements) {
            entry.give();
        }
    }
}
