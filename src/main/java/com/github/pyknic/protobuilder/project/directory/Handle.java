package com.github.pyknic.protobuilder.project.directory;

import java.nio.file.Path;
import javafx.beans.value.ObservableStringValue;

/**
 *
 * @author Emil Forslund
 */
public interface Handle {
    
    static String ICON_SIZE = "1.2em";
    
    Path getAbsolutePath();
    ObservableStringValue getText();
    javafx.scene.Node getGraphic();
    
}