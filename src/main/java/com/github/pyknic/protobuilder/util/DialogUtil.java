package com.github.pyknic.protobuilder.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 *
 * @author Emil Forslund
 */
public final class DialogUtil {
    
    public static void showConfirmation(String title, String message, Runnable action) {
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText("Do you want to proceed?");

        final Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            action.run();
        }
    }
    
    public static void showError(String title, String message, Throwable exception) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
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
    
    private DialogUtil() {}
}
