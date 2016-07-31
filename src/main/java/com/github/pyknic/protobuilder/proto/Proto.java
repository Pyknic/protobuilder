package com.github.pyknic.protobuilder.proto;

import javafx.beans.Observable;
import javafx.beans.value.ObservableStringValue;

/**
 *
 * @author Emil Forslund
 */
public interface Proto extends Observable {
    ObservableStringValue labelProperty();
}
