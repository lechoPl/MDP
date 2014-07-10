package userCode;

import enums.Action;
import world.State;
import world.WorldSim;

public class MyAgent extends AbstractAgent implements IAgent  {

    //user variables
    
    @Override
    public void setWorldSimulator(WorldSim simulator) {
        super.setWorldSimulator(simulator);
        
        // init user variables
    }
    
    @Override
    public void step() {
        // user code
    }

    @Override
    public double getUsability(State s) {
        // user code
    }
}
