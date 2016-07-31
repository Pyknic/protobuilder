package com.github.pyknic.protobuilder.proto.observable;

import com.github.pyknic.protobuilder.proto.Parameter;
import com.github.pyknic.protobuilder.proto.Type;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;

/**
 *
 * @author Emil Forslund
 */
public final class ParameterImpl extends AbstractObservable implements Parameter {
    
    private final StringProperty name;
    private final ObjectProperty<Type> type;
    private final ObjectProperty<Category> category;

    public ParameterImpl(String name, Type type) {
        this.name     = observe(new SimpleStringProperty(name));
        this.type     = observe(new SimpleObjectProperty<>(type));
        this.category = observe(new SimpleObjectProperty<>(Category.OPTIONAL));
    }

    @Override
    public ObjectProperty<Type> typeProperty() {
        return type;
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public ObjectProperty<Category> categoryProperty() {
        return category;
    }
    
    @Override
    public ObservableStringValue labelProperty() {
        return Bindings.createStringBinding(() -> {
            
            final StringBuilder labelBuilder = new StringBuilder(getName());
            labelBuilder.append(" : ");
            
            if (getType() == null) {
                labelBuilder.append("(unknown)");
            } else {
                labelBuilder.append(getType().getTypeName());
            }
            
            switch (getCategory()) {
                case REQUIRED : labelBuilder.append(" - REQUIRED"); break;
                case REPEATED : labelBuilder.append(" - REPEATED"); break;
                case OPTIONAL : break;
                default : throw new UnsupportedOperationException(
                    "Unknown enum constant '" + getCategory() + "'."
                );
            }
            
            return labelBuilder.toString();
        }, nameProperty(), typeProperty(), categoryProperty());
    }
}