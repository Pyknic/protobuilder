package com.github.pyknic.protobuilder.proto.observable;

import com.github.pyknic.protobuilder.proto.Document;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;

/**
 *
 * @author Emil Forslund
 */
public final class DocumentImpl extends AbstractObservable implements Document {
    
    private final StringProperty version;
    
    public DocumentImpl(String version) {
        this.version = observe(new SimpleStringProperty(version));
    }
    
    @Override
    public StringProperty versionProperty() {
        return version;
    }

    @Override
    public ObservableStringValue labelProperty() {
        return new SimpleStringProperty("Proto version: " + getVersion());
    }
}