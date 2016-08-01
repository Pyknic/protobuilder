package com.github.pyknic.protobuilder.project;

import com.github.pyknic.protobuilder.project.directory.impl.FolderHandleImpl;
import com.github.pyknic.protobuilder.project.directory.impl.FileHandleImpl;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import javafx.scene.control.TreeItem;
import java.util.Comparator;
import java.util.HashSet;
import com.github.pyknic.protobuilder.project.directory.FileHandle;
import com.github.pyknic.protobuilder.project.directory.Handle;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author Emil Forslund
 */
public final class DirectoryTreeUpdater {
    
    private final TreeItem<Handle> rootNode;
    private final DirectoryScanner scanner;
    
    public DirectoryTreeUpdater(TreeItem<Handle> rootNode) {
        this.rootNode = requireNonNull(rootNode);
        this.scanner  = new DirectoryScanner(this::updateBranch);
    }
    
    public void start() {
        scanner.start();
        updateBranch(rootNode.getValue().getAbsolutePath());
    }
    
    public void stop() {
        scanner.stop();
    }
    
    private void updateBranch(Path path) {
        final Path relative = rootNode.getValue().getAbsolutePath().relativize(path);
        final Iterator<Path> iterator = relative.iterator();
        
        final NodeWalker walker = new NodeWalker(rootNode);
        while (iterator.hasNext()) {
            final Path step = iterator.next();
            final Path expected = walker.path.resolve(step);
            
            if (Files.isDirectory(expected)) {
                // Remove all nodes from the tree that can not be found on disk.
                try {
                    final Set<Path> available = Files.list(walker.path).collect(toSet());
                    final Set<TreeItem<Handle>> remove = new HashSet<>();

                    walker.item.getChildren().stream()
                        .filter(item -> !available.contains(item.getValue().getAbsolutePath()))
                        .forEach(remove::add);

                    walker.item.getChildren().removeAll(remove);
                } catch (final IOException ex) {
                    throw new RuntimeException(
                        "Could not list available paths under path '" + 
                        walker.path + "'.", ex
                    );
                }
            }
            
            // Look for the path that was modified in the list.
            final Optional<TreeItem<Handle>> found =
                walker.item.getChildren().stream()
                    .filter(i -> i.getValue().getAbsolutePath().equals(expected))
                    .findAny();
            
            // If it exists in the tree, continue from here.
            if (found.isPresent()) {
                walker.walkTo(found.get());
                
            // If it does not exist, create it before continuing.
            } else {
                final TreeItem<Handle> newItem = new TreeItem<>();
                newItem.setExpanded(true);
                
                if (Files.isDirectory(expected)) {
                    newItem.setValue(newDirectory(expected));
                } else {
                    newItem.setValue(newFile(expected));
                }
                
                walker.item.getChildren().add(newItem);
                walker.item.getChildren().sort(COMPARATOR);
                
                if (Files.isDirectory(expected)) {
                    scanner.watch(expected);
                    walker.walkTo(newItem);
                }
            }
        }
        
        final Path finalPath = walker.item.getValue().getAbsolutePath();
        if (Files.isDirectory(finalPath)) {
            try {
                Files.list(walker.path).forEach(this::updateBranch);
            } catch (final IOException ex) {
                throw new RuntimeException(
                    "Error trying to parse leaf node.", ex
                );
            }
        }
    }
    
    private FolderHandleImpl newDirectory(Path path) {
        return new FolderHandleImpl(path);
    }
    
    private FileHandle newFile(Path path) {
        return new FileHandleImpl(path);
    }
    
    private final static class NodeWalker {
        
        private TreeItem<Handle> item;
        private Path path;
        
        public NodeWalker(TreeItem<Handle> origin) {
            this.item = origin;
            this.path = origin.getValue().getAbsolutePath();
        }
        
        public void walkTo(TreeItem<Handle> nextItem) {
            this.item = nextItem;
            this.path = nextItem.getValue().getAbsolutePath();
        }
    }
    
    private final static Comparator<TreeItem<Handle>> COMPARATOR =
        Comparator.comparing((TreeItem<Handle> ti) -> ti.getValue().getClass().getName())
            .thenComparing(ti -> ti.getValue().getText().get());
}
