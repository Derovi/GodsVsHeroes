package by.dero.gvh.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class BoosterInfo {
    private String name;
    @Builder.Default
    private double selfMultiplier = 1;
    @Builder.Default
    private double teamMultiplier = 1;
    @Builder.Default
    private double gameMultiplier = 1;
    @Builder.Default
    private int durationSec = 3600;
}
