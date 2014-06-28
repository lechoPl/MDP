package userCode;

import world.State;

public interface IUserCode {

    void setDiscount(double val);

    double getUsability(State s);

    void iterate();
}
