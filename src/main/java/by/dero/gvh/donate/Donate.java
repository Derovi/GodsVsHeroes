package by.dero.gvh.donate;

import by.dero.gvh.Plugin;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import ru.cristalix.core.invoice.IInvoiceService;
import ru.cristalix.core.invoice.Invoice;

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
        IInvoiceService.get().bill(player.getUniqueId(), Invoice.builder()
                .price(price)
                .description(description)
                .allowBonuses(true)
                .build()).thenAccept(invoiceResult -> {
            if (invoiceResult.isSuccess()) {
                System.out.println("Suc: " + price);
                Plugin.getInstance().getDonateData().save(
                        DonateInfo.builder().description(description).type(type).playerName(player.getName()).price(price).build());
                onSuccessful.run();
            } else {
                onError.run();
            }
        });
    }
}
