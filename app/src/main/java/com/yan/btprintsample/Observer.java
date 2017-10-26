package com.yan.btprintsample;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanweiqiang on 2017/10/23.
 */

public class Observer {

    private static List<Observable> observableList = new ArrayList<>();

    public static void trigger(String event) {
        for (Observable observable : observableList) {
            observable.action(event);
        }
    }

    public static void subscript(Observable observable) {
        observableList.add(observable);
    }

    public static void clear() {
        observableList.clear();
    }

    public static void remove(Observable observable) {
        if (!observableList.contains(observable)) {
            return;
        }
        observableList.remove(observable);
    }
}
