package world;

public class State implements Comparable<State>{
    protected int x;
    protected int y;
    
    public State(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }

    @Override
    public int compareTo(State t) {
        if(t == null) {
            return -1;
        }
        
        if(t.x == x && t.y == y) {
            return 0;
        }
        else {
            return t.x < x ? -1 : 1;
        }
    }
}
