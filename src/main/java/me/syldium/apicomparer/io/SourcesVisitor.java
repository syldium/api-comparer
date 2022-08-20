package me.syldium.apicomparer.io;

import me.syldium.apicomparer.model.SourcesContent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashSet;

/**
 * Walk through a file tree to collect the java sources.
 */
public class SourcesVisitor extends SimpleFileVisitor<Path> {

    private final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.java");
    private final SourcesContent sources = new SourcesContent(new HashSet<>());

    /**
     * Search for java sources in a folder and all its subfolders.
     *
     * @param start the folder to start with
     * @return all sources found
     * @throws IOException if an I/O error is thrown by a visitor method
     */
    public static @NotNull SourcesContent exploreFiles(@NotNull Path start) throws IOException {
        final SourcesVisitor visitor = new SourcesVisitor();
        Files.walkFileTree(start, visitor);
        return visitor.sources;
    }

    /**
     * Search for java sources in a zip-like file.
     *
     * @param zipPath the file to search in
     * @return all sources found
     * @throws IOException if the file doesn't exist or is not a zip
     */
    public static @NotNull SourcesContent exploreZip(@NotNull Path zipPath) throws IOException {
        try (final FileSystem fs = FileSystems.newFileSystem(URI.create("jar:" + zipPath.toUri()), Collections.emptyMap())) {
            return exploreFiles(fs.getPath("/"));
        }
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) {
        if (this.matcher.matches(path)) {
            this.sources.files().add(path.toString());
        }
        return FileVisitResult.CONTINUE;
    }
}
