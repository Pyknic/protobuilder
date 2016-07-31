package com.github.pyknic.protobuilder.controller;

import com.github.pyknic.protobuilder.proto.Parameter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author Emil Forslund
 */
public final class ParameterContextMenuController implements Initializable {
    
    private final Parameter selected;
    
    public ParameterContextMenuController(Parameter selected) {
        this.selected = requireNonNull(selected);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    @FXML
    void rename() {
        
    }
    
    @FXML
    void setCategory() {
        
    }
    
    @FXML
    void setType() {
        
    }
    
    @FXML
    void remove() {
        
    }
}
