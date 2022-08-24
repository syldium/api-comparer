package me.syldium.apicomparer.io;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Walk through a file tree to collect the java sources.
 */
public class SourcesFileVisitor extends SimpleFileVisitor<Path> {

    private final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.java");
    private final List<String> sources = new ArrayList<>();

    /**
     * Search for java sources in a folder and all its subfolders.
     *
     * @param start the folder to start with
     * @return all source files found
     * @throws IOException if an I/O error is thrown by a visitor method
     */
    public static @NotNull List<String> exploreFiles(@NotNull Path start) throws IOException {
        final SourcesFileVisitor visitor = new SourcesFileVisitor();
        Files.walkFileTree(start, visitor);
        return visitor.sources;
    }

    /**
     * Search for java sources in a zip-like file.
     *
     * @param zipPath the file to search in
     * @param workingDirectory the directory to
     * @return all sources found
     * @throws IOException if the file doesn't exist or is not a zip
     */
    public static @NotNull List<String> exploreZip(@NotNull Path zipPath, @NotNull Path workingDirectory) throws IOException {
        try (final FileSystem fs = FileSystems.newFileSystem(URI.create("jar:" + zipPath.toUri()), Collections.emptyMap());
             final Stream<Path> pathStream = Files.walk(fs.getPath("/"))) {
            final Iterator<Path> it = pathStream.iterator();
            final Path base = it.next();
            while (it.hasNext()) {
                final Path sourcePath = it.next();
                final Path targetPath = workingDirectory.resolve(base.relativize(sourcePath).toString());
                if (Files.isDirectory(sourcePath)) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.copy(sourcePath, targetPath);
                }
            }
            return exploreFiles(/*fs.getPath("/")*/workingDirectory);
        }
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) {
        if (this.matcher.matches(path)) {
            this.sources.add(path.toString());
        }
        return FileVisitResult.CONTINUE;
    }
}
