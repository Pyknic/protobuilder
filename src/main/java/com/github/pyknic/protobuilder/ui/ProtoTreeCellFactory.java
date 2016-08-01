/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.pyknic.protobuilder.ui;

import java.io.IOException;

import com.github.pyknic.protobuilder.controller.MessageContextMenuController;
import com.github.pyknic.protobuilder.controller.ParameterContextMenuController;
import com.github.pyknic.protobuilder.controller.SceneController;
import com.github.pyknic.protobuilder.proto.Message;
import com.github.pyknic.protobuilder.proto.Parameter;
import com.github.pyknic.protobuilder.proto.Proto;

import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

/**
 *
 * @author Emil Forslund
 * @author Simon Jonasson
 */
public class ProtoTreeCellFactory implements Callback<TreeView<Proto>, TreeCell<Proto>>{

    @Override
    public TreeCell<Proto> call(TreeView<Proto> param) {
        final ProtoTreeCell cell = new ProtoTreeCell();
        final ChangeListener<String> textChange = (ob, o, n) -> {
            cell.setText(n);
        };

        cell.itemProperty().addListener((ov, o, n) -> {
            if (o != null) {
            	System.out.println("o was null");
                o.labelProperty().removeListener(textChange);
            }

            if (n != null) {
            	System.out.println("n was null");
                n.labelProperty().addListener(textChange);
                cell.addDragDrop();
                cell.setText(n.labelProperty().get());
                cell.setGraphic(createGraphic(n));
                cell.setContextMenu(createContextMenu(n));
            } else {
            	System.out.println("n is null");
            	cell.removeDragDrop();	//TODO: Fix removal and adding of drag drop listeners
                cell.setText(null);
                cell.setGraphic(null);
                cell.setContextMenu(null);
            }
        });

        return cell;
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
                .style("-fx-fill:#66a4cc")
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
