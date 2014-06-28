package enums;

public enum FieldType {
    EMPTY,
    START,
    TERMINAL,
    FORBIDDEN,
    SPECIAL;
    
    public static char getChar(FieldType ft) {
        switch(ft) {
            case FORBIDDEN:
                return 'F';
            case SPECIAL:
                return 'B';
            case START:
                return 'S';
            case TERMINAL:
                return 'G';
            default:
                return ' ';
        }
    }
    
    public static FieldType getFieldType(char c) {
        switch(c) {
            case 'F':
                return FieldType.FORBIDDEN;
            case 'B':
                return FieldType.SPECIAL;
            case 'S':
                return FieldType.START;
            case 'G':
                return FieldType.TERMINAL;
            default:
                throw new IllegalArgumentException();
        }
    }
}
