
package enums;

public enum Action {
    UP ,
    DOWN,
    RIGHT,
    LEFT;
    
    public static String toString(Action a) {
        if(a == null)
            return "";
        
        switch(a) {
            case UP:
                return "^";
            case DOWN:
                return "v";
            case LEFT:
                return "<";
            case RIGHT:
                return ">";
            default:
                return "";
        }
    }
}
