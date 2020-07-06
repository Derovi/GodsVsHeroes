package by.dero.gvh.nmcapi.dragon;

public class DragonBase extends EmptyArmorStand {
    private ControlledDragon dragon;

    public DragonBase(ControlledDragon dragon) {
        super(dragon.getPlayer().getEyeLocation());
        this.dragon = dragon;
    }

    @Override
    public void B_() {
        dragon.update();
    }

    public ControlledDragon getDragon() {
        return dragon;
    }

    public void setDragon(ControlledDragon dragon) {
        this.dragon = dragon;
    }
}
