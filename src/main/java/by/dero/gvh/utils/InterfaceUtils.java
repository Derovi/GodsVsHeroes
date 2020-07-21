package by.dero.gvh.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InterfaceUtils {
    public static void changeName(ItemStack itemStack, String name) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
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
            String cop = removeColors(first.get(i));
            int f = -1, s = -1;
            for (int j = 0; j < cop.length(); j++) {
                if (Character.isDigit(cop.charAt(j))) {
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
            cop = removeColors(second.get(i));
            for (int j = 0; j < cop.length(); j++) {
                if (Character.isDigit(cop.charAt(j))) {
                    f = f == -1 ? j : f;
                    s = j;
                }
            }
            String ss = cop.substring(f, s + 1);
            if (!ans.get(i).getKey().equals(ss)) {
//                ans.get(i).setValue("§f" + ans.get(i).getKey() + "§8->§b" + ss);
                ans.set(i, Pair.of(ss, "§f" + ans.get(i).getKey() + "§8->§b" + ss));
            }
        }
        return ans;
    }
}
