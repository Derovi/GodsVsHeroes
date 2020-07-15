package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class SwordInfo extends ItemInfo {
    private int throwDamage;

    public SwordInfo(ItemDescription description) {
        super(description);
    }

    public int getThrowDamage() {
        return throwDamage;
    }

    public void setThrowDamage(int throwDamage) {
        this.throwDamage = throwDamage;
    }
}
