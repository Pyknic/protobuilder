package com.github.pyknic.protobuilder.controller;

import com.github.pyknic.protobuilder.project.directory.FolderHandle;
import com.github.pyknic.protobuilder.util.DialogUtil;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author Emil Forslund
 */
public final class FolderContextMenuController implements Initializable {
    
    private final FolderHandle selected;
    
    public FolderContextMenuController(FolderHandle selected) {
        this.selected = requireNonNull(selected);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    @FXML
    void newFolder() {
        final Path newPath = selected.getAbsolutePath().resolve("New Folder");
        try {
            Files.createDirectory(newPath);
        } catch (final IOException ex) {
            DialogUtil.showError(
                "Error Creating Folder", "The new folder '" + 
                newPath + "' could not be created.", ex
            );
        }
    }

    @FXML
    void newMessage() {
        final Path newPath = selected.getAbsolutePath().resolve("NewMessage.proto");
        try {
            Files.createFile(newPath);
        } catch (final IOException ex) {
            DialogUtil.showError(
                "Error Creating Folder", "The new folder '" + 
                newPath + "' could not be created.", ex
            );
        }
    }
    
    @FXML
    void newEnum() {
        
    }
    
    @FXML
    void remove() {
        final Path toRemove = selected.getAbsolutePath();
        try {
            Files.delete(toRemove);
        } catch (final IOException ex) {
            DialogUtil.showError(
                "Error Deleting Folder",
                "An error occured that prevented '" + 
                toRemove + "' from being deleted.", ex
            );
        }
    }
}
