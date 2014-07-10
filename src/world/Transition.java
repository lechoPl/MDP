package world;

public class Transition {
    public final State state;
    public double probability;
    
    public Transition(State s, double p) {
        state = s;
        probability = p;
    }
    
    public double getProbabilty() {
        return probability;
    }
    
    public void setProbabilty(double val) {
        probability = val;
    }
}
