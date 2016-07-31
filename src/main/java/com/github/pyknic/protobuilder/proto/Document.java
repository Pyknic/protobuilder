package com.github.pyknic.protobuilder.proto;

import javafx.beans.property.StringProperty;

/**
 *
 * @author Emil Forslund
 */
public interface Document extends Proto {

    default String getVersion() {
        return versionProperty().get();
    }
    
    StringProperty versionProperty();
    
}
