package me.syldium.apicomparer.io;

import me.syldium.apicomparer.model.SourcesContent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public final class SourcesProcessor {

    private SourcesProcessor() {}

    public static @NotNull SourcesContent process(@NotNull Path zipPath, @NotNull Path workingDirectory) throws IOException {
        final String[] sources = SourcesFileVisitor.exploreZip(zipPath, workingDirectory).toArray(new String[0]);
        return new SourcesCollector(sources).getSources();
    }

    public static @NotNull SourcesContent process(@NotNull Path zipPath) throws IOException {
        final Path temporary = Files.createTempDirectory("java-sources");
        final SourcesContent content = process(zipPath, temporary);
        try (Stream<Path> walk = Files.walk(temporary)) {
            final Iterable<Path> pathsToDelete = walk.sorted(Comparator.reverseOrder())::iterator;
            for (Path path : pathsToDelete) {
                Files.delete(path);
            }
        }
        return content;
    }
}
