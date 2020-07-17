package by.dero.gvh.bookapi;

import by.dero.gvh.nmcapi.NMCUtils;
import net.minecraft.server.v1_12_R1.EnumHand;
import net.minecraft.server.v1_12_R1.MojangsonParser;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class BookGUI {
    private Player owner;

    public BookGUI(Player owner) {
        this.owner = owner;
    }

    public void open() {
        ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
        try {
            NMCUtils.setNBT(itemStack, MojangsonParser.parse("{pages:[{\n" +
                    "  \"text\": \"Minecraft Tools book\\\\n\\\\ngtrhrthrth\",\n" +
                    "  \"clickEvent\": {\n" +
                    "    \"action\": \"run_command\",\n" +
                    "    \"value\": \"/say lel\"\n" +
                    "  }\n" +
                    "}]}"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        BookMeta meta = (BookMeta) itemStack.getItemMeta();
        itemStack.setItemMeta(meta);
        ItemStack copy = owner.getInventory().getItemInMainHand();
        owner.getInventory().setItemInMainHand(itemStack);
        ((CraftPlayer) owner).getHandle().a(CraftItemStack.asNMSCopy(itemStack), EnumHand.MAIN_HAND);
        owner.getInventory().setItemInMainHand(copy);
    }
}
