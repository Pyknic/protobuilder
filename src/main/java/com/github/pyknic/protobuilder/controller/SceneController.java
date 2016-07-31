package com.github.pyknic.protobuilder.controller;

import com.github.pyknic.protobuilder.proto.DefaultType;
import com.github.pyknic.protobuilder.proto.Message;
import com.github.pyknic.protobuilder.proto.Parameter;
import com.github.pyknic.protobuilder.proto.Proto;
import com.github.pyknic.protobuilder.proto.Type;
import com.github.pyknic.protobuilder.proto.observable.MessageImpl;
import com.github.pyknic.protobuilder.proto.observable.ParameterImpl;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * FXML Controller class
 *
 * @author Emil
 */
public final class SceneController implements Initializable {

    private @FXML MenuBar menubar;
    private @FXML TreeView<Proto> treeView;
    
    private final TreeItem<Proto> root;
    
    public SceneController() {
        root = new TreeItem<>();
    }

    /**
     * Initializes the controller class.
     * 
     * @param url  the url
     * @param rb   the resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        treeView.setCellFactory(view -> {
            final TreeCell<Proto> cell = new TreeCell<>();
            
            final ChangeListener<String> textChange = (ob, o, n) -> {
                cell.setText(n);
            };
            
            cell.itemProperty().addListener((ov, o, n) -> {
                if (o != null) {
                    o.labelProperty().removeListener(textChange);
                }
                
                if (n != null) {
                    n.labelProperty().addListener(textChange);
                    cell.setText(n.labelProperty().get());
                    cell.setGraphic(createGraphic(n));
                    cell.setContextMenu(createContextMenu(n));
                    
                } else {
                    cell.setText(null);
                    cell.setGraphic(null);
                    cell.setContextMenu(null);
                }
            });
            
            return cell;
        });
        
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
    
    @FXML
    void onNewProject(ActionEvent ev) {
        
    }
    
    @FXML
    void onOpenProject(ActionEvent ev) {
        
    }
    
    @FXML
    void onSave(ActionEvent ev) {
        
    }
    
    @FXML
    void onSaveAs(ActionEvent ev) {
        
    }
    
    @FXML
    void onClose(ActionEvent ev) {
        
    }
    
    private ContextMenu createContextMenu(Proto selected) {
        final String fxml;
        
        if (selected instanceof Message) {
            fxml = FXML_PREFIX + "Message" + FXML_SUFFIX;
        } else if (selected instanceof Parameter) {
            fxml = FXML_PREFIX + "Parameter" + FXML_SUFFIX;
        } else {
            throw new RuntimeException(
                "Unexpected cell value '" + selected + "'."
            );
        }
        
        final FXMLLoader loader = new FXMLLoader(
            SceneController.class.getResource(fxml)
        );
        
        loader.setControllerFactory(clazz -> {
            if (clazz == MessageContextMenuController.class) {
                @SuppressWarnings("unchecked")
                final Message message = (Message) selected;
                return new MessageContextMenuController(message);
            } else if (clazz == ParameterContextMenuController.class) {
                @SuppressWarnings("unchecked")
                final Parameter parameter = (Parameter) selected;
                return new ParameterContextMenuController(parameter);
            } else {
                throw new RuntimeException(
                    "Unknown controller class '" + clazz.getName() + "'."
                );
            }
        });
        
        try {
            final ContextMenu contextMenu = loader.load();
            return contextMenu;
        } catch (IOException ex) {
            throw new RuntimeException(
                "Error creating context menu from FXML file.", ex
            );
        }
    }
    
    private Node createGraphic(Proto selected) {
        if (selected instanceof Message) {
            return GlyphsBuilder.create(FontAwesomeIconView.class)
                .glyph(FontAwesomeIcon.ENVELOPE)
                .size(ICON_SIZE)
                .style("-fx-fill:#66cc8f")
                .build();
        } else if (selected instanceof Parameter) {
            return GlyphsBuilder.create(FontAwesomeIconView.class)
                .glyph(FontAwesomeIcon.CUBE)
                .size(ICON_SIZE)
                .style("-fx-fill:#91cc66")
                .build();
        } else {
            return null;
        }
    }
    
    private final static String ICON_SIZE = "1.2em";
    private final static String FXML_PREFIX = "/fxml/",
                                FXML_SUFFIX = "ContextMenu.fxml";
}
