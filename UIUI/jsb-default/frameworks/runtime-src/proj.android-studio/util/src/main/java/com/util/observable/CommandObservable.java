package com.util.observable;

import java.util.Observable;

/**
 * Created by WangYi on 2017/6/5.
 */

public class CommandObservable extends Observable {

    private String command;
    public void notifyChanged() {
        setChanged();
        notifyObservers(command);
    }
    public String getCommand() {

        return command;
    }

    public void setCommand(String cmd) {
        command = cmd;
    }


}

