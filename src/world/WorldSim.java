package world;

import enums.*;
import java.util.ArrayList;
import java.util.Random;

public class WorldSim {

    protected World world;
    protected State currentState;
    protected Random rand = new Random();
    
    public WorldSim(World w) {
        world = w;
        currentState = world.getStartState();
    }
    
    public int getN() {
        return world.getN();
    }
    
    public int getM() {
        return world.getM();
    }
    
    public void reset() {
        currentState = world.getStartState();
    }

    public State getCurrentState() {
        return currentState;
    }

    public double getCurrentReward() {
        return world.Reward(currentState);
    }
    
    public boolean isTermina(State s) {
        return world.isTermina(s);
    }
    
    public void executeAction(Action action) {
        double randowDobule = rand.nextDouble();

        double a = world.getA();
        double b = world.getB();

        if (randowDobule < a) {
            currentState = world.getFront(currentState, action);
        } else if (randowDobule < a + b) {
            currentState = world.getLeft(currentState, action);
        } else {
            currentState = world.getRigth(currentState, action);
        }
    }
    
    public Action[] getActions() {
        return world.getAllActions().toArray(new Action[world.getAllActions().size()]);
    }
    
    public ArrayList<State> getStates() {
        return world.ListStates();
    }
}
