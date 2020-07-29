package by.dero.gvh.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Booster {
    public Booster(String name, long startTime, long expirationTime, double bonus) {
        this.name = name;
        this.startTime = startTime;
        this.expirationTime = expirationTime;
        this.bonus = bonus;
    }

    private String name;
    private long startTime = 0;
    private long expirationTime = 0;
    private double bonus;

    public String getName() {
        return name;
    }

    public Booster setName(String name) {
        this.name = name;
        return this;
    }

    public long getStartTime() {
        return startTime;
    }

    public Booster setStartTime(long startTime) {
        this.startTime = startTime;
        return this;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public Booster setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
        return this;
    }

    public double getBonus() {
        return bonus;
    }

    public Booster setBonus(double bonus) {
        this.bonus = bonus;
        return this;
    }
}
