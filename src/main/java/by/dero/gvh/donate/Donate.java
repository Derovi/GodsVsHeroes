package by.dero.gvh.donate;

import by.dero.gvh.Plugin;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import ru.cristalix.core.invoice.IInvoiceService;
import ru.cristalix.core.invoice.Invoice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Builder
public class Donate {
    @Getter @Setter
    DonateType type;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private Runnable onSuccessful;

    @Builder.Default
    @Getter @Setter
    private Runnable onError = null;

    @Getter @Setter
    private int price;

    public void apply(Player player) {
//        Plugin.getInstance().getDonateData().save(
//                DonateInfo.builder().description(description).type(type).playerName(player.getName()).price(price).build());
//        onSuccessful.run();
        if (player.isOp()) {
            player.sendMessage("§aКуплено за опку");
            onSuccessful.run();
            return;
        }
        IInvoiceService.get().bill(player.getUniqueId(), Invoice.builder()
                .price(price)
                .description(description)
                .allowBonuses(true)
                .build()).thenAccept(invoiceResult -> {
            if (invoiceResult.isSuccess()) {
                Plugin.getInstance().getDonateData().save(
                        DonateInfo.builder()
                                .description(description)
                                .type(type)
                                .playerName(player.getName())
                                .date(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                                        .format(Calendar.getInstance().getTime()))
                                .price(price).build());
                onSuccessful.run();
            } else {
                player.sendMessage("§cУ вас не хватает кристалликов =(");
                onError.run();
            }
        });
    }
}
