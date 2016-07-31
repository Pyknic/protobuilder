package com.github.pyknic.protobuilder.proto;

import javafx.beans.Observable;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Emil Forslund
 */
public interface Type extends Observable {
    
    default String getTypeName() {
        return typeNameProperty().get();
    }
    
    StringProperty typeNameProperty();
}