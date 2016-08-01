package com.github.pyknic.protobuilder.project;

import com.github.pyknic.protobuilder.project.directory.impl.FolderHandleImpl;
import static com.github.pyknic.protobuilder.util.DialogUtil.showConfirmation;
import static com.github.pyknic.protobuilder.util.DialogUtil.showError;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.control.TreeItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TreeView;
import com.github.pyknic.protobuilder.project.directory.Handle;
import static java.util.Objects.requireNonNull;

/**
 * A helper class that handles saving and loading projects, showing appropriate
 * dialog messages depending on which methods are called.
 * 
 * @author Emil Forslund
 */
public final class ProjectHelper {
    
    private final static int MAX_DEPTH = 20;
    private final static int EMPTY = -1, SAVED = 0;
    private final static String DEFAULT_VERSION = "proto2";
    
    private final Stage stage;
    private final TreeView<Handle> treeView;
    private final AtomicReference<Path> currentPath;
    private final IntegerProperty saved;
    private DirectoryTreeUpdater updater;
    
    public ProjectHelper(Stage stage, TreeView<Handle> treeView) {
        this.currentPath = new AtomicReference<>(null);
        this.stage       = requireNonNull(stage);
        this.treeView    = requireNonNull(treeView);
        this.saved       = new SimpleIntegerProperty(EMPTY);
        this.updater     = null;

        stage.setOnCloseRequest(ev -> {
            ev.consume();
            close();
        });
    }
    
    /**************************************************************************/
    /* Boolean flag to determine if project has been saved.                   */
    /**************************************************************************/
    
    public void change() {
        saved.add(2);
    }
    
    public boolean isSaved() {
        return saved.get() == SAVED;
    }
    
    public ObservableBooleanValue savedProperty() {
        return saved.isEqualTo(SAVED); 
    }
    
    /**************************************************************************/
    /* Actions that can be called from FXML.                                  */
    /**************************************************************************/
    
    public void newProject() {
        if (hasChangesBeenMade()) { // Changes have been made
            showConfirmation("Create New Project", 
                "Are you sure you want to create a new project? Any unsaved " + 
                "changes to the current project will be gone.", 
                this::clearProject
            );
        } else {
            clearProject();
        }
    }
    
    public void save() {
        final Path path = currentPath.get();
        if (path == null) { showSaveProject(); } 
        else { saveTo(path); }
    }
    
    public void saveAs() {
        showSaveProject();
    }
    
    public void openProject() {
        if (hasChangesBeenMade()) { // Changes have been made
            showConfirmation("Open Existing Project", 
                "Are you sure you want to close this project? Any unsaved " + 
                "changes to the current project will be gone.", 
                this::showOpenProject
            );
        } else {
            showOpenProject();
        }
    }
    
    
    public void close() {
        if (hasChangesBeenMade()) {
            showConfirmation("Close Project", 
                "Are you sure you want to close this project? Any unsaved " + 
                "changes to the current project will be gone.", 
                this::exit
            );
        } else {
            exit();
        }
    }
    
    /**************************************************************************/
    /* Factories for various dialog messages.                                 */
    /**************************************************************************/
    
    private void showOpenProject() {
        final DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Open Project Directory");
        
        final Path defaultLocation = currentPath.get();
        if (defaultLocation != null
        &&  Files.isReadable(defaultLocation)
        &&  Files.isDirectory(defaultLocation)) {
            dirChooser.setInitialDirectory(defaultLocation.toFile());
        }
        
        final File selected = dirChooser.showDialog(stage);
        if (selected != null) {
            clearProject();
            final Path path = selected.toPath();
            currentPath.set(path);
            loadFrom(path);
            saved.set(SAVED);
        }
    }
    
    private void showSaveProject() {
        final DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Save Project Directory");
        
        final Path defaultLocation = currentPath.get();
        if (defaultLocation != null
        &&  Files.isReadable(defaultLocation)
        &&  Files.isDirectory(defaultLocation)) {
            dirChooser.setInitialDirectory(defaultLocation.toFile());
        }
        
        final File selected = dirChooser.showDialog(stage);
        if (selected != null) {
            saveTo(selected.toPath());
        }
    }
    
    /**************************************************************************/
    /* Internal methods for manipulating the project.                         */
    /**************************************************************************/
    
    private void exit() {
        if (updater != null) {
            updater.stop();
        }
        
        stage.close();
    }
    
    private boolean hasChangesBeenMade() {
        return saved.get() > 0;
    }
    
    private void clearProject() {
        try {
            if (updater != null) {
                updater.stop();
            }
            
            currentPath.set(null);
            treeView.setRoot(null);
            saved.set(EMPTY);
        } catch (final Exception ex) {
            showError("Error Clearing Project",
                "The existing project tree could not be cleared.", ex
            );
        }
    }
    
    private void saveTo(Path path) {
        try {
            // TODO Traverse project tree and create '.proto'-files
            saved.set(SAVED);
        } catch (final Exception ex) {
            showError("Error Saving Project",
                "An error occured that prevented the project from being saved.", 
                ex
            );
        }
    }
    
    private void loadFrom(Path folder) {
        final TreeItem<Handle> root = new TreeItem<>(new FolderHandleImpl(folder));
        
        if (updater != null) {
            updater.stop();
        }
        
        updater = new DirectoryTreeUpdater(root);
        treeView.setRoot(root);
        updater.start();
    }
}