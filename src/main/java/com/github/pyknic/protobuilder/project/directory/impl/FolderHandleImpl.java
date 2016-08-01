package com.github.pyknic.protobuilder.project.directory.impl;

import com.github.pyknic.protobuilder.project.directory.FolderHandle;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.nio.file.Path;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.scene.Node;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author Emil Forslund
 */
public final class FolderHandleImpl implements FolderHandle {
    
    private final Path absolutePath;
    private final StringProperty text;
    private final Node graphics;
    
    public FolderHandleImpl(Path path) {
        absolutePath = requireNonNull(path);
        text = new SimpleStringProperty(formatName(path));
        graphics = GlyphsBuilder.create(FontAwesomeIconView.class)
            .glyph(FontAwesomeIcon.FOLDER)
            .size(ICON_SIZE)
            .style("-fx-fill:#d0b37a")
            .build();
    }

    @Override
    public Path getAbsolutePath() {
        return absolutePath;
    }

    @Override
    public ObservableStringValue getText() {
        return text;
    }

    @Override
    public Node getGraphic() {
        return graphics;
    }
    
    private static String formatName(Path path) {
        return path.getFileName().toString();
    };
}