package com.github.pyknic.protobuilder.proto.observable;

import com.github.pyknic.protobuilder.proto.Parameter;
import static java.util.Collections.newSetFromMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 * @author Emil Forslund
 */
abstract class AbstractObservable implements Observable {
    
    private final Set<InvalidationListener> listeners;
    private final InvalidationListener invalidator;
    private final ListChangeListener<? extends Observable> listChangeListener;
    
    protected AbstractObservable() {
        this.listeners   = newSetFromMap(new ConcurrentHashMap<>());
        this.invalidator = o -> {
            listeners.forEach(l -> l.invalidated(this));
        };
        
        this.listChangeListener = (ListChangeListener.Change<? extends Parameter> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(p -> observe(p));
                }
                
                if (c.wasRemoved()) {
                    c.getRemoved().forEach(p -> unobserve(p));
                }
            }
        };
    }
    
    protected final <E extends Observable, L extends ObservableList<E>> L observeList(L list) {
        @SuppressWarnings("unchecked")
        final ListChangeListener<E> casted = (ListChangeListener<E>) listChangeListener;
        list.addListener(casted);
        return list;
    }
    
    protected final <T extends Observable> T observe(T other) {
        other.addListener(invalidator);
        return other;
    }
    
    protected final <T extends Observable> T unobserve(T other) {
        other.removeListener(invalidator);
        return other;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        listeners.remove(listener);
    }
}
