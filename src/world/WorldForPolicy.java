package world;

import enums.Action;

public class WorldForPolicy {

    protected final World world;

    public WorldForPolicy(World w) {
        world = w;
    }

    public int getN() {
        return world.getN();
    }
    
    public int getM() {
        return world.getM();
    }

    public State[] getStates() {
        return world.ListStates().toArray(new State[world.ListStates().size()]);
    }

    public Transition[] getTransitions(State s, Action a) {
        return world.Transitions(s, a).toArray(new Transition[world.Transitions(s, a).size()]);
    }

    public double getReward(State s) {
        return world.Reward(s);
    }

    public Action[] getActions() {
        return world.getAllActions().toArray(new Action[world.getAllActions().size()]);
    }

    public boolean isTerminal(State s) {
        return world.isTerminal(s);
    }
}
