package com.github.pyknic.protobuilder.project;

import com.github.pyknic.protobuilder.proto.Proto;
import com.github.pyknic.protobuilder.proto.observable.DocumentImpl;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import static java.util.Objects.requireNonNull;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * A helper class that handles saving and loading projects, showing appropriate
 * dialog messages depending on which methods are called.
 * 
 * @author Emil Forslund
 */
public final class ProjectHelper {
    
    private final static int EMPTY = -1, SAVED = 0;
    private final static String DEFAULT_VERSION = "proto2";
    
    private final Stage stage;
    private final AtomicReference<Path> currentPath;
    private final TreeItem<Proto> root;
    private final IntegerProperty saved;
    
    public ProjectHelper(Stage stage, TreeItem<Proto> root) {
        this.currentPath = new AtomicReference<>(null);
        this.stage = requireNonNull(stage);
        this.root  = requireNonNull(root);
        this.saved = new SimpleIntegerProperty(EMPTY);
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
                stage::close
            );
        } else {
            stage.close();
        }
    }
    
    /**************************************************************************/
    /* Factories for various dialog messages.                                 */
    /**************************************************************************/
    
    private void showConfirmation(String title, String message, Runnable action) {
        final Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText("Do you want to proceed?");

        final Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            action.run();
        }
    }
    
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
            loadFrom(path);
            currentPath.set(path);
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
    
    private void showError(String title, String message, Throwable exception) {
        final Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(exception.getClass().getSimpleName());
        alert.setContentText(message);

        // Create expandable Exception.
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);

        final Label label = new Label("Full Stack Trace:");
        final TextArea textArea = new TextArea(sw.toString());
        
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        final GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }
    
    /**************************************************************************/
    /* Internal methods for manipulating the project.                         */
    /**************************************************************************/
    
    private boolean hasChangesBeenMade() {
        return saved.get() > 0;
    }
    
    private void clearProject() {
        try {
            currentPath.set(null);
            root.setValue(new DocumentImpl(DEFAULT_VERSION));
            root.getChildren().clear();
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
    
    private void loadFrom(Path path) {
        try {
            // TODO Traverse path for '.proto'-files and populate project tree
            saved.set(SAVED);
        } catch (final Exception ex) {
            showError("Error Loading Project",
                "An error occured that prevented the project from being loaded.", 
                ex
            );
        }
    }
}
