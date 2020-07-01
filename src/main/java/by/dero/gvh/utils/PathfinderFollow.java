package by.dero.gvh.utils;

import net.minecraft.server.v1_12_R1.*;

public class PathfinderFollow extends PathfinderGoal {
    private final EntityInsentient a;   // entity
    private EntityLiving b;             // owner

    private final double f;             // speed
    private final float g;              // view distance

    private double c;                   // x
    private double d;                   // y
    private double e;                   // z

    public PathfinderFollow(EntityInsentient a, double speed, float view) {
        this.a = a;
        this.f = speed;
        this.g = view;
        this.a(1);
    }

    @Override
    public boolean a() {
        this.b = this.a.getGoalTarget();
        if (this.b == null) {
            return false;
        }
        if (this.b.h(this.a) > (double) this.g * this.g) {
            return false;
        }
        Vec3D var2 = RandomPositionGenerator.a((EntityCreature) this.a, (int) this.g, 7,
                new Vec3D(this.a.locX, this.a.locY, this.a.locZ));
        if (var2 == null) {
            return false;
        }

        this.c = this.b.locX;
        this.d = this.b.locY;
        this.e = this.b.locZ;
        return true;
    }

    @Override
    public void c() {
        this.a.getNavigation().a(this.c, this.d, this.e, this.f);
    }

    @Override
    public boolean b() {
        return !this.a.getNavigation().o();
    }

    @Override
    public void d() {
        this.b = null;
    }
}
