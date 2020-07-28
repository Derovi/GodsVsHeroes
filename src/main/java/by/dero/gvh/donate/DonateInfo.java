package by.dero.gvh.donate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class DonateInfo {
    @Getter @Setter
    DonateType type;

    @Getter @Setter
    String description;

    @Getter @Setter
    int price;

    @Getter @Setter
    String playerName;
}
