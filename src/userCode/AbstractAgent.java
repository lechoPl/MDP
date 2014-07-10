package userCode;

import world.WorldSim;

public abstract class AbstractAgent implements IAgent {

    protected WorldSim world;
    protected double discount = 1;

    @Override
    public void setWorldSimulator(WorldSim simulator) {
        world = simulator;
    }

    @Override
    public void setDiscount(double val) {
        discount = val;
    }

    @Override
    public void iterate() {

        if (world.isTerminal(world.getCurrentState())) {
            step();
        }

        while (!world.isTerminal(world.getCurrentState())) {
            step();
        }
    }
}
