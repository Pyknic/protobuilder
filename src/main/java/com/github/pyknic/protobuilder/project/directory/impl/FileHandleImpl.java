package com.github.pyknic.protobuilder.project.directory.impl;

import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.nio.file.Path;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.scene.Node;
import com.github.pyknic.protobuilder.project.directory.FileHandle;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author Emil Forslund
 */
public final class FileHandleImpl implements FileHandle {
    
    private final Path absolutePath;
    private final StringProperty text;
    private final Node graphics;
    
    public FileHandleImpl(Path path) {
        absolutePath = requireNonNull(path);
        text = new SimpleStringProperty(formatName(path));
        graphics = GlyphsBuilder.create(FontAwesomeIconView.class)
            .glyph(FontAwesomeIcon.ENVELOPE)
            .size(ICON_SIZE)
            .style("-fx-fill:#66a4cc")
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
        final String fileName = stripExtension(path.getFileName().toString());
        final StringBuilder name = new StringBuilder()
            .append(Character.toUpperCase(fileName.charAt(0)));

        if (fileName.length() > 1) {
            name.append(fileName.substring(1));
        }
        
        return name.toString();
    };
    
    private static String stripExtension(String str) {
        if (str.endsWith(".proto")) {
            return str.substring(0, str.length() - ".proto".length());
        } else return str;
    }
}