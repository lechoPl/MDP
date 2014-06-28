package userCode;

import world.WorldSim;

public interface IAgent extends IUserCode {

    void setWorldSimulator(WorldSim simulator);

    void step();
}
