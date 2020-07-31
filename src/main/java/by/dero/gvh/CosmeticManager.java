package by.dero.gvh;

import by.dero.gvh.model.Cosmetic;
import by.dero.gvh.model.CosmeticInfo;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class CosmeticManager {
    @Getter
    private final Map<String, CosmeticInfo> customizations = new HashMap<>();

    @Getter
    private final Map<UUID, Set<String>> playerCustomizations = new HashMap<>();

    public CosmeticManager() {
        // register heads
        register(CosmeticInfo.builder()
                .name("headDrop")
                .hero("all")
                .rarity(CosmeticInfo.Rarity.MYTHICAL)
                .material(Material.DIAMOND)
                .displayName("§4Падающие головы")
                .cost(119)
                .groupID(0)
                .build());


        // warrior
        register(CosmeticInfo.builder()  // blood sword
                .name("bloodySword")
                .hero("warrior")
                .rarity(CosmeticInfo.Rarity.RARE)
                .material(Material.DIAMOND_SWORD)
                .displayName("§4Кровавый меч")
                .cost(69)
                .groupID(1)
                .nbt(new CosmeticInfo.NBT("weapons", "bloodyvengeance"))
                .build());

        register(CosmeticInfo.builder()  // victory sword
                .name("victorySword")
                .hero("warrior")
                .rarity(CosmeticInfo.Rarity.MYTHICAL)
                .material(Material.DIAMOND_SWORD)
                .displayName("§bМеч победы")
                .cost(119)
                .groupID(1)
                .nbt(new CosmeticInfo.NBT("weapons", "azure_sabre"))
                .build());

        register(CosmeticInfo.builder()  // death mace
                .name("deathMace")
                .hero("warrior")
                .rarity(CosmeticInfo.Rarity.IMMORTAL)
                .material(Material.DIAMOND_SWORD)
                .displayName("§4Булава смерти")
                .cost(279)
                .groupID(1)
                .nbt(new CosmeticInfo.NBT("weapons", "bludgeon"))
                .build());

        // horseman
        register(CosmeticInfo.builder()  // hell axe
                .name("hellAxe")
                .hero("horseman")
                .rarity(CosmeticInfo.Rarity.MYTHICAL)
                .material(Material.DIAMOND_SWORD)
                .displayName("§4Топор ада")
                .cost(119)
                .groupID(2)
                .nbt(new CosmeticInfo.NBT("weapons", "waraxe"))
                .build());

        register(CosmeticInfo.builder()  // skeleton axe
                .name("skeletonAxe")
                .hero("horseman")
                .rarity(CosmeticInfo.Rarity.RARE)
                .material(Material.DIAMOND_SWORD)
                .displayName("§4Костяной топор")
                .cost(69)
                .groupID(2)
                .nbt(new CosmeticInfo.NBT("weapons", "skeletonAxe"))
                .build());

        // dovahkiin
        register(CosmeticInfo.builder()  // dragon sword
                .name("dragonSword")
                .hero("dovahkiin")
                .rarity(CosmeticInfo.Rarity.LEGENDARY)
                .material(Material.DIAMOND_SWORD)
                .displayName("§4Меч дракона")
                .cost(189)
                .groupID(3)
                .nbt(new CosmeticInfo.NBT("weapons", "dragon"))
                .build());

        // lucifer
        register(CosmeticInfo.builder()  // demonic sword
                .name("demoicSword")
                .hero("lucifer")
                .rarity(CosmeticInfo.Rarity.MYTHICAL)
                .material(Material.DIAMOND_SWORD)
                .displayName("§4Демонический меч")
                .cost(119)
                .groupID(4)
                .nbt(new CosmeticInfo.NBT("weapons", "demonic"))
                .build());

        register(CosmeticInfo.builder()  // devil sword
                .name("devilSword")
                .hero("lucifer")
                .rarity(CosmeticInfo.Rarity.IMMORTAL)
                .material(Material.DIAMOND_SWORD)
                .displayName("§4Меч сатаны")
                .cost(279)
                .groupID(4)
                .nbt(new CosmeticInfo.NBT("weapons", "spider"))
                .build());

        // paladin
        register(CosmeticInfo.builder()  // fairy sword
                .name("devilSword")
                .hero("paladin")
                .rarity(CosmeticInfo.Rarity.UNCOMMON)
                .material(Material.CLAY_BALL)
                .displayName("§4Сказочный меч")
                .cost(29)
                .groupID(5)
                .nbt(new CosmeticInfo.NBT("ether", "paladinsword"))
                .build());
        register(CosmeticInfo.builder()  // lost sword
                .name("lostSword")
                .hero("paladin")
                .rarity(CosmeticInfo.Rarity.RARE)
                .material(Material.DIAMOND_SWORD)
                .displayName("§4Утерянный меч")
                .cost(69)
                .groupID(5)
                .nbt(new CosmeticInfo.NBT("weapons", "crimsoncleaver"))
                .build());

        // assassin
        register(CosmeticInfo.builder()  // altair blade
                .name("altairBlade")
                .hero("assassin")
                .rarity(CosmeticInfo.Rarity.LEGENDARY)
                .material(Material.CLAY_BALL)
                .displayName("§4Клинок альтаира")
                .cost(189)
                .groupID(6)
                .nbt(new CosmeticInfo.NBT("ether", "hid_blade"))
                .build());

        register(CosmeticInfo.builder()  // Ezio Espadron
                .name("ezioEspadron")
                .hero("assassin")
                .rarity(CosmeticInfo.Rarity.MYTHICAL)
                .material(Material.DIAMOND_SWORD)
                .displayName("§4Эспадрон Эцио")
                .cost(119)
                .groupID(6)
                .nbt(new CosmeticInfo.NBT("weapons", "claymore"))
                .build());
    }

    public static int getWeaponGroup(String name) {
        int group = 1;
        switch (name) {
            case "horseman":
                group = 2;
                break;
            case "dovahkiin":
                group = 3;
                break;
            case "lucifer":
                group = 4;
                break;
            case "paladin":
                group = 5;
                break;
            case "assassin":
                group = 6;
                break;
        }
        return group;
    }

    public void loadPlayer(Player player) {
        Set<String> customizations = new HashSet<>();
        for (Cosmetic customization :
                Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName()).getCosmetics().values()) {
            if (customization.isEnabled()) {
                customizations.add(customization.getName());
            }
        }
        playerCustomizations.put(player.getUniqueId(), customizations);
    }

    public boolean isEnabled(Player player, String customization) {
        if (playerCustomizations.containsKey(player.getUniqueId())) {
            return playerCustomizations.get(player.getUniqueId()).contains(customization);
        } else {
            HashMap<String, Cosmetic> customizations =
                    Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName()).getCosmetics();
            return customizations.containsKey(customization) && customizations.get(customization).isEnabled();
        }
    }

    public CosmeticInfo getByGroup(Player player, int groupID) {
        Set<String> cosmetics;
        if (playerCustomizations.containsKey(player.getUniqueId())) {
            cosmetics = playerCustomizations.get(player.getUniqueId());
        } else {
            cosmetics = new HashSet<>();
            for (Cosmetic customization :
                    Plugin.getInstance().getPlayerData().getPlayerInfo(player.getName()).getCosmetics().values()) {
                if (customization.isEnabled()) {
                    cosmetics.add(customization.getName());
                }
            }
        }
        for (String name : cosmetics) {
            if (customizations.get(name).getGroupID() == groupID) {
                return customizations.get(name);
            }
        }
        return null;
    }

    public void register(CosmeticInfo customization) {
        customizations.put(customization.getName(), customization);
    }
}
