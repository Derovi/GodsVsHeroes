package by.dero.gvh.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InterfaceUtils {
    public static ItemStack changeName(ItemStack itemStack, String name) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static void changeLore(ItemStack itemStack, List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
    }
    
    public static String removeColors(String str) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '§') {
                i++;
            } else {
                builder.append(str.charAt(i));
            }
        }
        return builder.toString();
    }
    
    public static List<Pair<String, String>> getDifference(List<String> first, List<String> second) {
        List<Pair<String, String>> ans = new ArrayList<>(first.size());
        for (int i = 0; i < first.size(); i++) {
            String cop = first.get(i);
            int f = -1, s = -1;
            for (int j = 0; j < cop.length(); j++) {
                if ((j == 0 || cop.charAt(j-1) != '§') && Character.isDigit(cop.charAt(j))) {
                    f = f == -1 ? j : f;
                    s = j;
                }
            }
            if (f == -1) {
                ans.add(Pair.of(" ", " "));
                continue;
            }
            cop = cop.substring(f, s + 1);
            ans.add(Pair.of(cop, cop));
            f = -1;
            s = -1;
            cop = second.get(i);
            for (int j = 0; j < cop.length(); j++) {
                if ((j == 0 || cop.charAt(j-1) != '§') && Character.isDigit(cop.charAt(j))) {
                    f = f == -1 ? j : f;
                    s = j;
                }
            }
            String ss = cop.substring(f, s + 1);
            if (!ans.get(i).getKey().equals(ss)) {
                ans.set(i, Pair.of(ss, "§f" + ans.get(i).getKey() + "§8->§b" + removeColors(ss)));
            }
        }
        return ans;
    }
    
    public static String replaceLast(String from, String was, String will) {
        boolean good;
        for (int i = from.length() - was.length(); i >= 0; i--) {
            good = true;
            for (int j = i; j < i + was.length(); j++) {
                if (from.charAt(j) != was.charAt(j-i)) {
                    good = false;
                    break;
                }
            }
            if (good) {
                return from.substring(0, i) + will + from.substring(i + was.length());
            }
        }
        return from;
    }
    
    public static String getLeftTimeMinuteString(int sec) {
        StringBuilder str = new StringBuilder();
        if (sec >= 3600) {
            int h = sec / 3600;
            str.append("§b").append(h);
            if (h / 10 != 1 && h % 10 == 1) {
                str.append(" §fчас ");
            } else if (h / 10 != 1 && 2 <= h % 10 && h % 10 <= 4) {
                str.append(" §fчаса ");
            } else {
                str.append(" §fчасов ");
            }
        }
        if (sec >= 60) {
            int m = sec / 60 % 60;
            if (m / 10 != 1 && m % 10 == 1) {
                str.append("§b").append(m).append(" §fминута");
            } else if (m / 10 != 1 && 2 <= m % 10 && m % 10 <= 4) {
                str.append("§b").append(m).append(" §fминуты");
            } else {
                str.append("§b").append(m).append(" §fминут");
            }
        }
        return str.toString();
    }
    
    public static String getLeftTimeString(int sec) {
        StringBuilder str = new StringBuilder();
        if (sec >= 3600) {
            int h = sec / 3600;
            str.append("§b").append(h);
            if (h / 10 != 1 && h % 10 == 1) {
                str.append(" §fчас ");
            } else if (h / 10 != 1 && 2 <= h % 10 && h % 10 <= 4) {
                str.append(" §fчаса ");
            } else {
                str.append(" §fчасов ");
            }
        }
        if (sec >= 60) {
            int m = sec / 60 % 60;
            if (m / 10 != 1 && m % 10 == 1) {
                str.append("§b").append(m).append(" §fминута");
            } else if (m / 10 != 1 && 2 <= m % 10 && m % 10 <= 4) {
                str.append("§b").append(m).append(" §fминуты");
            } else {
                str.append("§b").append(m).append(" §fминут");
            }
        }
        if (sec < 60) {
            if (sec == 1) {
                return "§b" + sec + " §fсекунда";
            } else if (2 <= sec && sec <= 4) {
                return "§b" + sec + " §fсекунды";
            } else {
                return "§b" + sec + " §fсекунд";
            }
        }
        return str.toString();
    }
}
