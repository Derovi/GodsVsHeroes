package by.dero.gvh.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class IntTopEntry {
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private int value;

    @Getter
    @Setter
    private int order;
}
