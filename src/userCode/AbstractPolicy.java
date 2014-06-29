package userCode;

import world.WorldForPolicy;

public abstract class AbstractPolicy implements IPolicy {

    protected WorldForPolicy world;
    protected double discount = 1;

    @Override
    public void setWorldForPolicy(WorldForPolicy worldForPolicy) {
        world = worldForPolicy;
    }

    @Override
    public void setDiscount(double val) {
        discount = val;
    }

}
