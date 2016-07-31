package com.github.pyknic.protobuilder.proto.observable;

import com.github.pyknic.protobuilder.proto.Message;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;

/**
 *
 * @author Emil Forslund
 */
public final class MessageImpl extends AbstractObservable implements Message {
    
    private final StringProperty typeName;
    
    public MessageImpl(String typeName) {
        this.typeName = observe(new SimpleStringProperty(typeName));
    }
    
    @Override
    public StringProperty typeNameProperty() {
        return typeName;
    }
    
    @Override
    public ObservableStringValue labelProperty() {
        return typeNameProperty();
    }
}
