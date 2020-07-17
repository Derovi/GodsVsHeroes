package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;
import org.bukkit.Particle;

public class EagleVisionInfo extends ItemInfo {
    private double radius;
    private Long glowTime;
    private Particle searchParticle = Particle.DRAGON_BREATH;

    public EagleVisionInfo(ItemDescription description) {
        super(description);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Long getGlowTime() {
        return glowTime;
    }

    public void setGlowTime(Long glowTime) {
        this.glowTime = glowTime;
    }

    public Particle getSearchParticle() {
        return searchParticle;
    }

    public void setSearchParticle(Particle searchParticle) {
        this.searchParticle = searchParticle;
    }
}
