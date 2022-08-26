package me.syldium.apicomparer;

import me.syldium.apicomparer.io.SourcesCollector;
import me.syldium.apicomparer.model.SourcesContent;
import me.syldium.apicomparer.model.type.JavaType;
import me.syldium.apicomparer.model.type.MethodParameter;
import me.syldium.apicomparer.model.type.MethodSignature;
import me.syldium.apicomparer.model.type.TypeDeclaration;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class ParserTest {

    @TempDir
    private Path workingDirectory;

    @Test
    void helloWorld() throws IOException {
        final MethodSignature constructor = new MethodSignature(
                Modifier.PRIVATE,
                "HelloWorld",
                List.of()
        );
        final MethodSignature main = new MethodSignature(
                Modifier.PUBLIC | Modifier.STATIC,
                "main",
                List.of(new MethodParameter(new JavaType.Array(new JavaType.Simple("String"), 1), "args"))
        );
        final SourcesContent content = content(Path.of("parser/helloworld/HelloWorld.java"));
        assertEquals(
                new TypeDeclaration.ClassOrInterface(
                        Modifier.FINAL,
                        "HelloWorld",
                        List.of(),
                        List.of(constructor, main)
                ),
                content.find("HelloWorld")
        );
    }

    SourcesContent content(@NotNull Path path) throws IOException {
        try (InputStream stream = ParserTest.class.getResourceAsStream("/src/" + path)) {
            final Path target = this.workingDirectory.resolve(path);
            Files.createDirectories(target.getParent());
            Files.copy(stream, target);
            return new SourcesCollector(new String[]{target.toString()}).getSources();
        }
    }
}
