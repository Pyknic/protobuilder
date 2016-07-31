package com.github.pyknic.protobuilder.controller;

import com.github.pyknic.protobuilder.proto.Message;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author Emil Forslund
 */
public final class MessageContextMenuController implements Initializable {
    
    private final Message selected;
    
    public MessageContextMenuController(Message selected) {
        this.selected = requireNonNull(selected);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    @FXML
    void addInt32() {
        
    }
    
    @FXML
    void addInt64() {
        
    }
    
    @FXML
    void addFloat() {
        
    }
    
    @FXML
    void addDouble() {
        
    }
    
    @FXML
    void addBoolean() {
        
    }
    
    @FXML
    void addString() {
        
    }
    
    @FXML
    void newMessage() {
        
    }
    
    @FXML
    void newEnum() {
        
    }
}
