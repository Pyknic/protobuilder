package com.github.pyknic.protobuilder.proto;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Emil Forslund
 * @author Simon Jonasson
 */
public enum DefaultType implements Type {
    
    INT_32("int32"),
    INT_64("int64"),
    FLOAT("float"),
    DOUBLE("double"),
    STRING("string");
    
    private final String typeName;
    
    DefaultType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public StringProperty typeNameProperty() {
        return new SimpleStringProperty(typeName);
    }

    @Override
    public void addListener(InvalidationListener listener) {}

    @Override
    public void removeListener(InvalidationListener listener) {}
}