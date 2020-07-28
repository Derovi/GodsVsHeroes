package by.dero.gvh.model.kits;

import lombok.Getter;

import java.util.HashMap;

public class DonateKitManager {
    interface DonateKitBuilder {
        DonateKit build(String playerName);
    }

    @Getter
    private final HashMap<String, DonateKitBuilder> donateKitBuilders = new HashMap<>();

    public DonateKitManager() {
        donateKitBuilders.put("kit1", (DonateKitBuilder) playerName -> {
            DonateKit donateKit = new DonateKit("kit1", playerName, 100);
            //donateKit.add();
            return donateKit;
        });
    }

    public DonateKit buildKit(String playerName, String kitName) {
        return donateKitBuilders.get(kitName).build(playerName);
    }
}
