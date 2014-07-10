package logic;

import enums.Action;
import java.util.ArrayList;
import userCode.IAgent;
import userCode.IPolicy;
import userCode.IUserCode;
import world.State;
import world.World;
import world.WorldForPolicy;
import world.WorldSim;

public class Logic {

    protected IUserCode userCode;
    protected World world;
    protected WorldSim worldSim;
    protected WorldForPolicy worldForPolicy;

    protected ArrayList<Double[][]> usabilityHistory = new ArrayList<>();
    protected ArrayList<Action[][]> policyHistory = new ArrayList<>();

    protected Action[][] optimalActions;
    protected Double[][] currentUsability;

    protected Double[][] storedUsability = null;
    protected Action[][] storedPolicy = null;

    protected double discount = 1;

    protected final int MAX_ITERATIONS = 15000;

    public ArrayList<Double[][]> GetUsabilityHistory() {
        return usabilityHistory;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World w) {
        world = w;
        worldSim = new WorldSim(w);
        worldForPolicy = new WorldForPolicy(w);

        if (userCode != null) {
            if (IAgent.class.isInstance(userCode)) {
                setAgent((IAgent) userCode);
            }

            if (IPolicy.class.isInstance(userCode)) {
                setPolicy((IPolicy) userCode);
            }
        }

        usabilityHistory = new ArrayList<>();
        storedUsability = null;
        policyHistory = new ArrayList<>();
        storedPolicy = null;
        optimalActions = null;
        currentUsability = null;
    }

    public void reset() {
        usabilityHistory = new ArrayList<>();
        policyHistory = new ArrayList<>();
        optimalActions = null;
        currentUsability = null;

        if (world != null) {
            setWorld(world);
        }

        if (userCode != null) {
            if (IAgent.class.isInstance(userCode)) {
                setAgent((IAgent) userCode);
            }

            if (IPolicy.class.isInstance(userCode)) {
                setPolicy((IPolicy) userCode);
            }
        }
    }

    public Action[][] getOptimalActions() {
        return optimalActions;
    }

    public Double[][] getCurrentUsefulness() {
        return currentUsability;
    }

    public void setAgent(IAgent loadAgent) {

        if (world != null) {
            loadAgent.setWorldSimulator(worldSim);
        }
        if (loadAgent != null) {
            loadAgent.setDiscount(discount);
        }
        userCode = loadAgent;

        usabilityHistory = new ArrayList<>();
        policyHistory = new ArrayList<>();
        optimalActions = null;
        currentUsability = null;
    }

    public void setPolicy(IPolicy loadPolicy) {
        if (world != null) {
            loadPolicy.setWorldForPolicy(worldForPolicy);
        }
        if (loadPolicy != null) {
            loadPolicy.setDiscount(discount);
        }
        userCode = loadPolicy;

        usabilityHistory = new ArrayList<>();
        policyHistory = new ArrayList<>();
        optimalActions = null;
        currentUsability = null;
    }

    public void iterateeAgent(int i) {
        if (userCode == null || !IAgent.class.isInstance(userCode)) {
            return;
        }

        for (int j = 0; j < i; j++) {
            ((IAgent) userCode).iterate();

            currentUsability = new Double[world.getN()][world.getM()];
            for (int x = 0; x < world.getN(); x++) {
                for (int y = 0; y < world.getM(); y++) {
                    currentUsability[x][y] = userCode.getUsability(new State(x, y));
                }
            }
            usabilityHistory.add(currentUsability);

            optimalActions = new Action[world.getN()][world.getM()];
            for (int x = 0; x < world.getN(); x++) {
                for (int y = 0; y < world.getM(); y++) {
                    optimalActions[x][y] = bestMove(x, y);

                }
            }
            policyHistory.add(optimalActions);
        }
    }

    public void stepAgent(int i) {
        if (userCode == null || !IAgent.class.isInstance(userCode)) {
            return;
        }

        for (int j = 0; j < i; j++) {
            ((IAgent) userCode).step();

            if (worldSim.isTerminal(worldSim.getCurrentState())) {
                currentUsability = new Double[world.getN()][world.getM()];
                for (int x = 0; x < world.getN(); x++) {
                    for (int y = 0; y < world.getM(); y++) {
                        currentUsability[x][y] = userCode.getUsability(new State(x, y));
                    }
                }

                optimalActions = new Action[world.getN()][world.getM()];
                for (int x = 0; x < world.getN(); x++) {
                    for (int y = 0; y < world.getM(); y++) {
                        optimalActions[x][y] = bestMove(x, y);

                    }
                }

                usabilityHistory.add(currentUsability);
                policyHistory.add(optimalActions);
            }
        }

        currentUsability = new Double[world.getN()][world.getM()];
        for (int x = 0; x < world.getN(); x++) {
            for (int y = 0; y < world.getM(); y++) {
                currentUsability[x][y] = userCode.getUsability(new State(x, y));
            }
        }

        optimalActions = new Action[world.getN()][world.getM()];
        for (int x = 0; x < world.getN(); x++) {
            for (int y = 0; y < world.getM(); y++) {
                optimalActions[x][y] = bestMove(x, y);

            }
        }

    }

    public void setDiscount(double d) {
        discount = d;

        if (userCode != null) {
            userCode.setDiscount(discount);
        }

        usabilityHistory = new ArrayList<>();
        optimalActions = null;
        currentUsability = null;
    }

    protected boolean endCalculate() {

        for (int x = 0; x < world.getN(); x++) {
            for (int y = 0; y < world.getM(); y++) {
                double delta = Math.abs(usabilityHistory.get(usabilityHistory.size() - 2)[x][y]
                        - currentUsability[x][y]);

                if (delta > 0.00001) {
                    return true;
                }
            }
        }

        return false;
    }

    public void calculatePolicy() {
        if (userCode == null || !IPolicy.class.isInstance(userCode) || world == null) {
            return;
        }

        while (usabilityHistory.size() < 2) {
            iteratePolicy(1);
        }

        while (endCalculate()) {
            if (world.getR() > 0 && usabilityHistory.size() > MAX_ITERATIONS) {
                break;
            }

            iteratePolicy(1);
        }
    }

    protected boolean isForbiden(int x, int y) {
        if (world != null) {
            if (world.isOutsideBoard(x, y)) {
                return true;
            }

            if (world.isForbidden(x, y)) {
                return true;
            }
        }

        return false;
    }

    protected double getValue(double f, double l, double r) {
        double result = 0;

        if (world != null) {
            result = world.getA() * f + (l + r) * world.getB();
        }

        return result;
    }

    protected Action bestMove(int x, int y) {
        double current = currentUsability[x][y];

        double l = isForbiden(x - 1, y) ? current : currentUsability[x - 1][y];
        double r = isForbiden(x + 1, y) ? current : currentUsability[x + 1][y];
        double u = isForbiden(x, y + 1) ? current : currentUsability[x][y + 1];
        double d = isForbiden(x, y - 1) ? current : currentUsability[x][y - 1];

        Action result = Action.LEFT;
        double max = getValue(l, u, d);

        double temp = getValue(u, l, r);
        if (temp > max) {
            max = temp;
            result = Action.UP;
        }

        temp = getValue(r, u, d);
        if (temp > max) {
            max = temp;
            result = Action.RIGHT;
        }

        temp = getValue(d, r, l);
        if (temp > max) {
            max = temp;
            result = Action.DOWN;
        }

        return result;
    }

    public void iteratePolicy(int i) {
        if (userCode == null || !IPolicy.class.isInstance(userCode) || world == null) {
            return;
        }

        for (int j = 0; j < i; j++) {
            currentUsability = new Double[world.getN()][world.getM()];
            
            ((IPolicy) userCode).iterate();

            for (int x = 0; x < world.getN(); x++) {
                for (int y = 0; y < world.getM(); y++) {
                    currentUsability[x][y] = userCode.getUsability(new State(x, y));
                }
            }

            usabilityHistory.add(currentUsability);

            optimalActions = new Action[world.getN()][world.getM()];
            for (int x = 0; x < world.getN(); x++) {
                for (int y = 0; y < world.getM(); y++) {
                    optimalActions[x][y] = bestMove(x, y);
                }
            }
            policyHistory.add(optimalActions);
        }

    }

    public State getCurrentAgentState() {
        if (userCode == null || !IAgent.class.isInstance(userCode) || worldSim == null) {
            return null;
        }

        return worldSim.getCurrentState();
    }

    public void StoreUsability() {
        if (currentUsability != null) {
            storedUsability = (Double[][]) currentUsability.clone();
        }
    }

    public Double[][] GetStoredUsability() {
        return storedUsability;
    }

    public ArrayList<Action[][]> GetPolicyHistory() {
        return policyHistory;
    }

    public void StorePolicy() {
        if (optimalActions != null) {
            storedPolicy = (Action[][]) optimalActions.clone();
        }
    }

    public Action[][] GetStoredPolicy() {
        return storedPolicy;
    }

}
