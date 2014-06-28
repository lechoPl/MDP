package world;

import enums.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * World is board where:
 * - left-up corner is point (0,0)
 * - right-down corner is (N-1, M-1)
 */
public class World {

    protected String name;

    protected int N;
    protected int M;

    //a + b + b = 1
    protected double a;
    protected double b;

    protected double r;

    protected Field board[][];

    protected ArrayList<State> listStates;
    protected ArrayList<Action> allActions;

    protected boolean refresh = false;

    /**
     * @return list of all states
     */
    public ArrayList<State> ListStates() {
        if (listStates == null || refresh) {
            refresh = false;

            listStates = new ArrayList<>();

            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    if (!isForbidden(i, j)) {
                        listStates.add(new State(i, j));
                    }
                }
            }
        }

        return listStates;
    }

    public ArrayList<Action> getAllActions() {
        if (allActions == null) {
            allActions = new ArrayList<>();
            for (Action a : Action.values()) {
                allActions.add(a);
            }
        }

        return allActions;
    }

    /**
     * @param s state
     * @return null if state is terminal else return all actions
     */
    public ArrayList<Action> Actions(State s) {
        if (board[s.getX()][s.getY()] != null
                && board[s.getX()][s.getY()].type == FieldType.TERMINAL) {
            return null;
        }

        if (allActions == null) {
            allActions = new ArrayList<>();
            for (Action a : Action.values()) {
                allActions.add(a);
            }
        }
        return allActions;
    }

    /**
     * @param s current state
     * @param action action
     * @return list of pair (s, p),
     * s - new state,
     * p - probability of finding in it
     */
    public ArrayList<Transaction> Transactions(State s, Action action) {
        ArrayList<Transaction> result = new ArrayList<>();

        result.add(new Transaction(getFront(s, action), this.a));
        Transaction tempTransaction;

        State left = getLeft(s, action);
        double leftProbability = this.b;
        tempTransaction = ContainsState(result, left);
        if (tempTransaction != null) {
            leftProbability += tempTransaction.probability;
            result.remove(tempTransaction);
        }
        result.add(new Transaction(left, leftProbability));

        State right = getRigth(s, action);
        double rightProbability = this.b;
        tempTransaction = ContainsState(result, right);
        if (tempTransaction != null) {
            rightProbability += tempTransaction.probability;
            result.remove(tempTransaction);
        }
        result.add(new Transaction(right, rightProbability));

        return result;
    }

    public synchronized static World loadFile(String path, String fileName,
            String fileExtenstion) throws Exception {

        try {
            World result = new World();

            result.name = fileName.substring(0, fileName.length()
                    - fileExtenstion.length());

            List<String> lines = Files.readAllLines(Paths.get(path + fileName),
                    Charset.defaultCharset());

            String[] firstLine = lines.get(0).split(" ");
            if (firstLine.length != 5) {
                throw new Exception("Error. Parse file: " + fileName
                        + " Wrong first line.");
            }

            result.N = Integer.parseInt(firstLine[0]);
            result.M = Integer.parseInt(firstLine[1]);
            result.a = Double.valueOf(firstLine[2].replace(',', '.'));
            result.b = Double.parseDouble(firstLine[3].replace(',', '.'));
            result.r = Double.parseDouble(firstLine[4].replace(',', '.'));

            result.board = new Field[result.N][result.M];
            for (int i = 0; i < result.N; i++) {
                for (int j = 0; j < result.M; j++) {
                    result.board[i][j] = new Field(FieldType.EMPTY, 0);
                }
            }

            for (int i = 1; i < lines.size(); i++) {
                try {
                    String line[] = lines.get(i).split(" ");
                    FieldType ft = FieldType.getFieldType(line[0].toCharArray()[0]);
                    int x = Integer.parseInt(line[1]);
                    int y = Integer.parseInt(line[2]);
                    double r = line.length == 4
                            ? Double.parseDouble(line[3].replace(',', '.'))
                            : 0;

                    result.board[x][y] = new Field(ft, r);
                } catch (Exception e) {
                    throw new Exception("Error. Parse file: " + fileName
                            + ". Line: " + i + ". ", e);
                }
            }

            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public String toString() {
        String result = String.format("%d %d %f %f %f\n", N, M, a, b, r);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (board[i][j] == null) {
                    continue;
                }

                switch (board[i][j].type) {
                    case START:
                    case FORBIDDEN:
                        result += String.format("%c %d %d\n",
                                FieldType.getChar(board[i][j].type), i, j);
                        break;
                    case SPECIAL:
                    case TERMINAL:
                        result += String.format("%c %d %d %f\n",
                                FieldType.getChar(board[i][j].type), i, j,
                                board[i][j].reward);
                        break;
                    default:
                    // do nothing
                }
            }
        }

        return result;
    }

    public double Reward(State s) {
        if (isOutsideBoard(s.x, s.y)) {
            throw new IllegalArgumentException("Wrong state");
        }

        if (board[s.x][s.y] != null) {
            switch (board[s.x][s.y].type) {
                case SPECIAL:
                case TERMINAL:
                    return board[s.x][s.y].reward;
                default:
                    return r;
            }
        }

        return r;
    }

    ;
    
    public State getStartState() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (board[i][j].type == FieldType.START) {
                    return new State(i, j);
                }
            }
        }

        return null;
    }

    public boolean isTermina(State s) {
        if (isOutsideBoard(s.x, s.y)) {
            return false;
        }

        return board[s.x][s.y].type == FieldType.TERMINAL;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public void setA(double val) {
        if (val < 0 || a > 1) {
            throw new IllegalArgumentException("Value of a is to big");
        }

        a = val;
        b = (1.0 - a) / 2.0;
    }

    public int getN() {
        return N;
    }

    public int getM() {
        return M;
    }

    public synchronized void setSize(int n, int m) {
        Field[][] newBoard = new Field[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (i < N && j < M) {
                    newBoard[i][j] = board[i][j];
                } else {
                    newBoard[i][j] = new Field(FieldType.EMPTY, 0);
                }
            }
        }

        this.board = newBoard;

        this.N = n;

        this.M = m;

        this.refresh = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String str) {
        name = str;
    }

    public Field getField(int x, int y) {
        return board[x][y];
    }

    public void setField(int x, int y, Field field) {
        board[x][y] = field;
    }

    public void setR(double val) {
        this.r = val;
    }

    public double getR() {
        return this.r;
    }

    public World copy() {
        World w = new World();
        w.name = this.name;
        w.N = this.N;
        w.M = this.M;
        w.a = this.a;
        w.b = this.b;
        w.r = this.r;

        w.board = new Field[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                w.board[i][j] = new Field(board[i][j].type, board[i][j].reward);
            }
        }

        return w;
    }

    //--------------------------------------------------------------------------
    //--    protected functions
    //--------------------------------------------------------------------------
    public boolean isOutsideBoard(int x, int y) {
        return x < 0 || x >= N || y < 0 || y >= M;
    }

    public boolean isForbidden(int x, int y) {
        return board[x][y] != null && board[x][y].type == FieldType.FORBIDDEN;
    }

    /**
     * @param list
     * @param s
     * @return return Transaction with state s if exist or null
     */
    protected Transaction ContainsState(ArrayList<Transaction> list, State s) {
        for (Transaction t : list) {
            if (t.state == s) {
                return t;
            }
        }

        return null;
    }

    /**
     * Action a is direction!
     * - UP-left is (s.x - 1, s.y)
     * - DOWN-left is (s.x + 1, s.y)
     *
     * @param s State
     * @param action Action
     * @return state on the left from action. if is wall or forbidden return s
     */
    public State getLeft(State s, Action action) {
        int x = s.getX();
        int y = s.getY();

        switch (action) {
            case DOWN:
                x++;
                break;
            case LEFT:
                y--;
                break;
            case UP:
                x--;
                break;
            case RIGHT:
                y++;
                break;
        }

        return getNewState(x, y, s);
    }

    /**
     * Action a is direction!
     * - UP-right is (s.x + 1, s.y)
     * - DOWN-right is (s.x - 1, s.y)
     *
     * @param s state
     * @param action action
     * @return state on the right from action. if is wall or forbidden return s
     */
    public State getRigth(State s, Action action) {
        int x = s.getX();
        int y = s.getY();

        switch (action) {
            case DOWN:
                x--;
                break;
            case LEFT:
                y++;
                break;
            case UP:
                x++;
                break;
            case RIGHT:
                y--;
                break;
        }

        return getNewState(x, y, s);
    }

    /**
     * @param s state
     * @param action action (direction)
     * @return new state or if is wall or forbidden return s
     */
    public State getFront(State s, Action action) {
        int x = s.getX();
        int y = s.getY();

        switch (action) {
            case DOWN:
                y--;
                break;
            case LEFT:
                x--;
                break;
            case UP:
                y++;
                break;
            case RIGHT:
                x++;
                break;
        }

        return getNewState(x, y, s);
    }

    protected State getNewState(int x, int y, State s) {
        if (isOutsideBoard(x, y) || isForbidden(x, y)) {
            return s;
        }

        return new State(x, y);
    }
}
