package com.github.pyknic.protobuilder.controller;

import com.github.pyknic.protobuilder.project.ProjectHelper;
import com.github.pyknic.protobuilder.project.directory.FileHandle;
import com.github.pyknic.protobuilder.project.directory.FolderHandle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import com.github.pyknic.protobuilder.project.directory.Handle;
import java.io.IOException;
import static java.util.Objects.requireNonNull;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;

/**
 * FXML Controller class
 *
 * @author Emil Forslund
 */
public final class SceneController implements Initializable {

    private @FXML MenuItem miSave;
    private @FXML TreeView<Handle> treeView;
    
    private final Stage stage;
    private ProjectHelper helper;
    
    public SceneController(Stage stage) {
        this.stage = requireNonNull(stage);
    }

    /**
     * Initializes the controller class.
     * 
     * @param url  the url
     * @param rb   the resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        helper = new ProjectHelper(stage, treeView);
        miSave.disableProperty().bind(helper.savedProperty());
        
        treeView.setCellFactory(view -> {
            final TreeCell<Handle> cell = new TreeCell<>();
            final ChangeListener<String> textChange = (ob, o, n) -> {
                cell.setText(n);
            };
            
            cell.itemProperty().addListener((ov, o, n) -> {
                if (o != null) {
                    o.getText().removeListener(textChange);
                }
                
                if (n != null) {
                    n.getText().addListener(textChange);
                    cell.setText(n.getText().get());
                    cell.setGraphic(n.getGraphic());
                    cell.setContextMenu(createContextMenu(n));
                } else {
                    cell.setText(null);
                    cell.setGraphic(null);
                    cell.setContextMenu(null);
                }
            });
            
            DragDropUtil.add(cell, "DD", (source, target) -> {
            	if( (source instanceof TreeCell<?>) && (target instanceof TreeCell<?>) ){
            		TreeCell<Handle> sourceCell = (TreeCell<Handle>) source;
            		TreeCell<Handle> targetCell = (TreeCell<Handle>) target;
            		
            		TreeItem<Handle> sourceTree = sourceCell.getTreeItem();
            		TreeItem<Handle> targetTree = targetCell.getTreeItem();
            		
//            		if(targetCell.getItem() instanceof Parameter                // <-- Parameter has been removed until file mappings has been implemented properly.
//    				|| targetTree.getParent().equals(sourceTree) ){
//            			return;
//            		}
            		
            		sourceTree.getParent().getChildren().remove(sourceTree);
            		targetTree.getChildren().add(sourceTree);
            		            		
            	}
            } );
            
            return cell;
        });
        
        treeView.setShowRoot(false);
    }
    
    @FXML void onNewProject(ActionEvent ev) { helper.newProject(); }
    @FXML void onOpenProject(ActionEvent ev) { helper.openProject(); }
    @FXML void onSave(ActionEvent ev) { helper.save(); }
    @FXML void onSaveAs(ActionEvent ev) { helper.saveAs(); }
    @FXML void onClose(ActionEvent ev) { helper.close(); }
    
    private ContextMenu createContextMenu(Handle selected) {
        final String fxml;
        
        if (selected instanceof FileHandle) {
            fxml = FXML_PREFIX + "File" + FXML_SUFFIX;
        /*} else if (selected instanceof Parameter) {
            fxml = FXML_PREFIX + "Parameter" + FXML_SUFFIX;
        */} else if (selected instanceof FolderHandle) {
            fxml = FXML_PREFIX + "Folder" + FXML_SUFFIX;
        } else {
            throw new RuntimeException(
                "Unexpected cell value '" + selected + "'."
            );
        }
        
        final FXMLLoader loader = new FXMLLoader(
            SceneController.class.getResource(fxml)
        );
        
        loader.setControllerFactory(clazz -> {
            if (clazz == FileContextMenuController.class) {
                @SuppressWarnings("unchecked")
                final FileHandle file = (FileHandle) selected;
                return new FileContextMenuController(file);
            /*} else if (clazz == ParameterContextMenuController.class) {
                @SuppressWarnings("unchecked")
                final Parameter parameter = (Parameter) selected;
                return new ParameterContextMenuController(parameter);
            */} else if (clazz == FolderContextMenuController.class) {
                @SuppressWarnings("unchecked")
                final FolderHandle folder = (FolderHandle) selected;
                return new FolderContextMenuController(folder);
            } else {
                throw new RuntimeException(
                    "Unknown controller class '" + clazz.getName() + "'."
                );
            }
        });
        
        try {
            final ContextMenu contextMenu = loader.load();
            return contextMenu;
        } catch (final IOException ex) {
            throw new RuntimeException(
                "Error creating context menu from FXML file.", ex
            );
        }
    }
    
    private final static String FXML_PREFIX = "/fxml/",
                                FXML_SUFFIX = "ContextMenu.fxml";
}