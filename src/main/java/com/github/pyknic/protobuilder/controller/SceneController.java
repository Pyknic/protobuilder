package com.github.pyknic.protobuilder.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.github.pyknic.protobuilder.project.ProjectHelper;
import com.github.pyknic.protobuilder.proto.Proto;
import com.github.pyknic.protobuilder.proto.Type;
import com.github.pyknic.protobuilder.proto.observable.MessageImpl;
import com.github.pyknic.protobuilder.proto.observable.ParameterImpl;
import com.github.pyknic.protobuilder.ui.ProtoTreeCellFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Emil Forslund
 */
public final class SceneController implements Initializable {

    private @FXML MenuItem miSave;
    private @FXML TreeView<Proto> treeView;
    
    private final TreeItem<Proto> root;
    private final ProjectHelper helper;
    
    public SceneController(Stage stage) {
        this.root   = new TreeItem<>();
        this.helper = new ProjectHelper(stage, root);
    }

    /**
     * Initializes the controller class.
     * 
     * @param url  the url
     * @param rb   the resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        miSave.disableProperty().bind(helper.savedProperty());
        
        treeView.setCellFactory( new ProtoTreeCellFactory() );        
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        
        
        final TreeItem<Proto> item0 = new TreeItem<>(new MessageImpl("Vector1f"));
        final TreeItem<Proto> item1 = new TreeItem<>(new MessageImpl("Vector2f"));
        
        root.getChildren().add(item0);
        root.getChildren().add(item1);
        root.getChildren().add(new TreeItem<>(new MessageImpl("Vector3f")));
        
        item1.getChildren().add(new TreeItem<>(new ParameterImpl("first", (Type) item0.getValue())));
        item1.getChildren().add(new TreeItem<>(new ParameterImpl("second", (Type) item0.getValue())));
    }
    
    @FXML void onNewProject(ActionEvent ev) { helper.newProject(); }
    @FXML void onOpenProject(ActionEvent ev) { helper.openProject(); }
    @FXML void onSave(ActionEvent ev) { helper.save(); }
    @FXML void onSaveAs(ActionEvent ev) { helper.saveAs(); }
    @FXML void onClose(ActionEvent ev) { helper.close(); }
    
}