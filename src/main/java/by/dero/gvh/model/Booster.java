package by.dero.gvh.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class Booster {
    private String name;
    private long startTime;
    private long expirationTime;
    private double bonus;
}
