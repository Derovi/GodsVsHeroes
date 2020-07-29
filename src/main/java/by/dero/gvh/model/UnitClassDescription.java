package by.dero.gvh.model;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

public class UnitClassDescription {
    @Getter @Setter
    private String name;
    @Getter @Setter
    private int cost;
    @Getter @Setter
    private int maxHP;
    @Getter @Setter
    private int cristCost;
    @Getter @Setter
    private List<String> itemNames = new LinkedList<>();
}
