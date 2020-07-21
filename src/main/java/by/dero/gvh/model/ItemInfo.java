package by.dero.gvh.model;

import by.dero.gvh.model.annotations.CustomDamage;
import by.dero.gvh.model.annotations.DynamicCustomization;
import by.dero.gvh.model.annotations.PotionItem;
import by.dero.gvh.model.annotations.StaticCustomization;
import by.dero.gvh.nmcapi.NMCUtils;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ItemInfo {
    static class EnchantInfo {
        public EnchantInfo(final String name, final int level, final boolean visible) {
            this.name = name;
            this.level = level;
            this.visible = visible;
        }

        public String getName() {
            return name;
        }

        public int getLevel() {
            return level;
        }

        public boolean isVisible() {
            return visible;
        }

        private final String name;
        private final int level;
        private final boolean visible;
    }

    private Material material = null;
    private final List<EnchantInfo> enchantments = new LinkedList<>();
    private String displayName = null;
    private List<String> lore = null;
    private int amount = 1;
    private int cooldown = 5;
    private int cost = 5;
    private final ItemDescription description;
    private ItemStack itemStack;
    private Method dynamicCustomizer = null;

    public ItemInfo(ItemDescription description) {
        this.description = description;
    }

    public void prepare(int level) {
        if (material == null) {
            material = description.getMaterial();
        }
        if (displayName == null) {
            if (description.getDisplayName() != null) {
                displayName = parseString(description.getDisplayName());
            } else {
                displayName = "§c" + Lang.get("items." + description.getName()) + " §9" + romeNumber(level);
            }
        }
        if (lore == null) {
            lore = new ArrayList<>();
            for (String string : description.getLore()) {
                lore.add(parseString(string));
            }
        }

        itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        for (ItemInfo.EnchantInfo enchantInfo : enchantments) {
            itemMeta.addEnchant(Enchantment.getByName(enchantInfo.getName()), enchantInfo.getLevel(), enchantInfo.isVisible());
        }
        itemMeta.setLore(lore);
        itemMeta.setDisplayName(displayName);
        itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        itemMeta.addEnchant(Enchantment.DURABILITY, Integer.MAX_VALUE, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);
        if (getClass().isAnnotationPresent(PotionItem.class)) {
            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            potionMeta.setBasePotionData(new PotionData(getClass().getAnnotation(PotionItem.class).potionType()));
            itemStack.setItemMeta(potionMeta);
        }
        for (Method method : getClass().getMethods()) {
            if (method.isAnnotationPresent(StaticCustomization.class)) {
                try {
                    method.invoke(null, itemStack, this);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (method.isAnnotationPresent(DynamicCustomization.class)) {
                dynamicCustomizer = method;
            }
        }
        NBTTagCompound compound = NMCUtils.getNBT(itemStack);
        compound.set("custom", new NBTTagString(description.getName()));
        for (Field field : getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(CustomDamage.class)) {
                try {
                    NBTTagList modifiers = new NBTTagList();
                    NBTTagCompound damage = new NBTTagCompound();
                    damage.set("AttributeName", new NBTTagString("generic.attackDamage"));
                    damage.set("Name", new NBTTagString("generic.attackDamage"));
                    damage.set("Amount", new NBTTagInt(field.getInt(this)));
                    damage.set("Operation", new NBTTagInt(0));
                    damage.set("UUIDLeast", new NBTTagInt(894654));
                    damage.set("UUIDMost", new NBTTagInt(2872));
                    damage.set("Slot", new NBTTagString("mainhand"));
                    modifiers.add(damage);
                    compound.set("AttributeModifiers", modifiers);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        NMCUtils.setNBT(itemStack, compound);
    }

    public String romeNumber(int number) {
        switch (number) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
        }
        return Integer.toString(number);
    }

    public ItemStack getItemStack(Player owner) {
        if (dynamicCustomizer != null) {
            try {
                ItemStack result = itemStack.clone();
                dynamicCustomizer.invoke(this, result, this, owner);
                return result;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return itemStack;
    }

    public String parseString(String string) {
        StringBuilder result = new StringBuilder();
        for (int idx = 0; idx < string.length(); ++idx) {
            if (idx + 1 != string.length() && string.charAt(idx) == '%' && string.charAt(idx + 1) == '%') {
                int second = 0;
                StringBuilder dataBuilder = new StringBuilder();
                for (int j = idx + 2; j < string.length() - 1; ++j) {
                    if (string.charAt(j) == '%' && string.charAt(j + 1) == '%') {
                        second = j;
                        break;
                    }
                    dataBuilder.append(string.charAt(j));
                }
                String data = dataBuilder.toString();
                if (data.contains(",")) {
                    try {
                        int comaPos = data.indexOf(',');
                        String fieldName = data.substring(0, comaPos);
                        double multiplier = Double.parseDouble(data.substring(comaPos + 1));
                        double fieldValue = getField(fieldName).getDouble(this);
                        result.append(new DecimalFormat("#.##",
                                DecimalFormatSymbols.getInstance(Locale.ENGLISH)).format(fieldValue * multiplier));
                    } catch (Exception ex) {
                        System.out.println("Field with multiplier not found: " + data + " in " + description.getName());
                        ex.printStackTrace();
                    }
                } else {
                    try {
                        result.append(getField(data).get(this));
                    } catch (Exception ex) {
                        System.out.println("Field not found: " + data + " in " + description.getName());
                    }
                }
                if (second != 0) {
                    idx = second + 1;
                    continue;
                }
            }
            result.append(string.charAt(idx));
        }
        return result.toString();
    }

    private Field getField(String name) throws Exception {
        Field field = null;
        try {
            field = getClass().getDeclaredField(name);
        } catch (Exception ex) {
            field = ItemInfo.class.getDeclaredField(name);
        }
        field.setAccessible(true);
        return field;
    }

    public List<EnchantInfo> getEnchantments() {
        return enchantments;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Material getMaterial() {
        return material == null ? description.getMaterial() : material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getDisplayName() {
        return displayName == null ? description.getDisplayName() : displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getLore() {
        return lore == null ? description.getLore() : lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public ItemDescription getDescription() {
        return description;
    }
}
