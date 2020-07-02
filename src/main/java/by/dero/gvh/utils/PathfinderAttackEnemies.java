package by.dero.gvh.utils;

import com.google.common.base.Predicate;
import net.minecraft.server.v1_12_R1.EntityCreature;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.PathfinderGoalNearestAttackableTarget;

import javax.annotation.Nullable;
import java.util.List;

public class PathfinderAttackEnemies<T extends EntityLiving> extends PathfinderGoalNearestAttackableTarget<T> {
    public PathfinderAttackEnemies(EntityCreature entitycreature, Class<T> oclass, int i, boolean flag, boolean flag1, @Nullable Predicate<? super T> predicate) {
        super(entitycreature, oclass, i, flag, flag1, predicate);
    }

    //this.a - attack class
    //this.a(double) - get territory
    //this.i - view distance
    //this.c - predicate if need
    //this.b - distance predicate
    @Override
    public boolean a() {
        List<T> list = this.e.world.a(this.a, this.a(this.i()), this.c);
        if (list.isEmpty()) {
            return false;
        } else {
            list.sort(this.b);
            this.d = list.get(0);
            return true;
        }
    }
}
