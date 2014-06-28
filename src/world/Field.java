package world;

import enums.FieldType;

public class Field {
    public FieldType type = FieldType.EMPTY;
    public double reward = 0;
    
    public Field(FieldType t, double r) {
        type = t;
        reward = r;
    }
}
