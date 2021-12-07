package sample;

import java.util.LinkedList;

public interface State{

    LinkedList<State> children = new LinkedList<State>();

    public void setChildren();
    public LinkedList<State> getChildren();
}
