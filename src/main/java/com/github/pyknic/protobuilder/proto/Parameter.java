package com.github.pyknic.protobuilder.proto;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Emil Forslund
 */
public interface Parameter extends Proto {
    
    default Type getType() {
        return typeProperty().get();
    }
    
    ObjectProperty<Type> typeProperty();
    
    default String getName() {
        return nameProperty().get();
    }
    
    StringProperty nameProperty();
    
    default Category getCategory() {
        return categoryProperty().get();
    }
    
    ObjectProperty<Category> categoryProperty();
    
    enum Category {
        REQUIRED,
        OPTIONAL,
        REPEATED
    }
}
