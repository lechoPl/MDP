package userCode;

import enums.Action;
import world.State;
import world.Transaction;
import world.WorldForPolicy;

public class IVpolicy extends AbstractPolicy implements IPolicy {

    Double[][] ussfuless = null;
    Double[][] lastUssfuless = null;
    
    @Override
    public void setWorldForPolicy(WorldForPolicy worldForPolicy) {
        super.setWorldForPolicy(worldForPolicy);
        
        ussfuless = new Double[world.getN()][world.getM()];
    }
    
    @Override
    public double getUsability(State s) {
        Double u = ussfuless[s.getX()][s.getY()];
        return u != null ? u : 0;
    }

    @Override
    public void iterate() {
        lastUssfuless = ussfuless;
        ussfuless = new Double[world.getN()][world.getM()];

        for (State s : world.getStates()) {
            if (world.isTerminal(s)) {
                ussfuless[s.getX()][s.getY()] = world.getReward(s);
            } else {

                double max = -Double.MAX_VALUE;
                for (Action a : world.getActions()) {
                    double temp = 0;

                    for (Transaction t : world.getTransactions(s, a)) {
                        Double u = lastUssfuless[t.state.getX()][t.state.getY()];
                        u = u != null ? u : 0;

                        temp += u * t.probability;
                    }

                    if (temp > max) {
                        max = temp;
                    }
                }

                double val = world.getReward(s) + discount * max;

                ussfuless[s.getX()][s.getY()] = val;
            }
        }
    }

}
