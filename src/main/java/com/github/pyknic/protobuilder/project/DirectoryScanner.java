package com.github.pyknic.protobuilder.project;

import com.github.pyknic.protobuilder.util.DialogUtil;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author Emil Forslund
 */
public final class DirectoryScanner {
    
    private final ExecutorService executor;
    private final WatchService watcher;
    private final Consumer<Path> listener;
    private final Map<WatchKey, Path> watchedPaths;
    private final Semaphore semaphore;
    
    public DirectoryScanner(Consumer<Path> listener) {
        this(listener, new ForkJoinPool(1));
    }
    
    public DirectoryScanner(Consumer<Path> listener, ExecutorService executor) {
        this.listener     = requireNonNull(listener);
        this.executor     = requireNonNull(executor);
        this.watchedPaths = new ConcurrentHashMap<>();
        
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (final IOException ex) {
            throw new RuntimeException(
                "Error creating file system watcher.", ex
            );
        }
        
        this.semaphore = new Semaphore(1);
    }
    
    public void start() {
        if (!executor.isShutdown()) {
            executor.submit(() -> {
                try {
                    while (!executor.isShutdown()) {
                        final WatchKey key = watcher.poll(1, TimeUnit.SECONDS);
                        if (key != null && key.isValid()) {
                            for (final WatchEvent<?> watchEvent : key.pollEvents()) {
                                final Kind<?> kind = watchEvent.kind();
                                
                                if (StandardWatchEventKinds.OVERFLOW == kind) {
                                    continue;
                                }
                                
                                semaphore.acquire();
                                final Path path = watchedPaths.get(key);
                                semaphore.release();
                                
                                if (path != null) {
                                    listener.accept(path);
                                } else {
                                    System.out.println("Cancelling key since to corresponding path could be found.");
                                    key.cancel();
                                }
                                
                                if (key.reset()) { 
                                    break; 
                                }
                            }
                        }
                    }
                } catch (final InterruptedException ex) {
                    System.out.println("Watcher service was interrupted.");
                }
            });
        } else {
            throw new RuntimeException(
                "Could not initiate new watch task since the watcher service " + 
                "is already shutdown."
            );
        }
    }
    
    public void stop() {
        try {
            watcher.close();
            executor.shutdown();
            try {executor.awaitTermination(3, TimeUnit.SECONDS);}
            catch (final InterruptedException ex) {
                DialogUtil.showError("Shutdown Task Timed Out", 
                    "The directory watch service did not shutdown before the " + 
                    "stopping task timed out. Some changes might not have been " + 
                    "saved properly.", ex
                );
            }
        } catch (final IOException ex) {
            DialogUtil.showError("Error shutting down watcher service", 
                "An error occured that prevented the watcher service from " + 
                "being shutdown.", ex
            );
        }
    }
    
    public void watch(Path path) {
        if (!executor.isShutdown()) {
            try {
                semaphore.acquire();
                watchedPaths.put(
                    path.register(watcher, 
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_DELETE
                    ), path
                );
                semaphore.release();
                
                Files.list(path)
                    .forEach(p -> {
                        listener.accept(p);
                        
                        if (Files.isDirectory(p)) {
                            watch(p);
                        }
                    });
            } catch (final IOException ex) {
                throw new RuntimeException(
                    "Error watching filesystem for changes.", ex
                );
            } catch (final InterruptedException ex) {
                throw new RuntimeException(
                    "Error initiating watch due to interrupt.", ex
                );
            }
        } else {
            throw new RuntimeException(
                "Could not watch path '" + path.toString() + 
                "' since watcher service is shutdown."
            );
        }
    }
}