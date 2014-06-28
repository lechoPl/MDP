package world;

public class Transaction {
    public final State state;
    public double probability;
    
    public Transaction(State s, double p) {
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
