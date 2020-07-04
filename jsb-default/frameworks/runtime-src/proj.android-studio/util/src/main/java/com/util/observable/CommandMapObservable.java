package com.util.observable;

import java.util.Observable;

/**
 * Created by WangYi on 2017/6/5.
 */

public class CommandMapObservable extends Observable {

    private CommandMapBean mBean;
    public void notifyChanged() {
        setChanged();
        notifyObservers(mBean);
    }
    public CommandMapBean getCommandMap() {

        return mBean;
    }

    public void setCommandMap(CommandMapBean bean) {
        mBean = bean;
    }

}

