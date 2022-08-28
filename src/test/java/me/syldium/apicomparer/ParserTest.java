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

    private static final JavaType STRING_TYPE = new JavaType.Simple("String");

    @TempDir
    private Path workingDirectory;

    @Test
    void helloWorldTest() throws IOException {
        final MethodSignature constructor = new MethodSignature(
                Modifier.PRIVATE,
                null,
                "HelloWorld",
                List.of()
        );
        final MethodSignature main = new MethodSignature(
                Modifier.PUBLIC | Modifier.STATIC,
                JavaType.Primitive.VOID,
                "main",
                List.of(new MethodParameter(new JavaType.Array(STRING_TYPE, 1), "args"))
        );
        final SourcesContent content = content(Path.of("parser/helloworld/HelloWorld.java"));
        assertEquals(
                new TypeDeclaration.ClassOrInterface(
                        Modifier.FINAL,
                        "HelloWorld",
                        null,
                        List.of(),
                        List.of(),
                        List.of(constructor, main)
                ),
                content.find("HelloWorld")
        );
    }

    @Test
    void enumTest() throws IOException {
        final JavaType enumExampleType = new JavaType.Simple("EnumExample");
        final MethodSignature from = new MethodSignature(
                Modifier.STATIC,
                enumExampleType,
                "from",
                List.of(new MethodParameter(JavaType.Primitive.BOOLEAN, "value"))
        );
        final MethodSignature toString = new MethodSignature(
                Modifier.PUBLIC,
                STRING_TYPE,
                "toString",
                List.of()
        );
        final SourcesContent content = content(Path.of("parser/type/EnumExample.java"));
        assertEquals(
                new TypeDeclaration.Enum(
                        0,
                        "EnumExample",
                        List.of("PRESENT", "ABSENT"),
                        List.of(new MethodParameter(JavaType.Primitive.INT, "VALUES_LEN")),
                        List.of(from, toString)
                ),
                content.find("EnumExample")
        );
    }

    @Test
    void genericWithInnerClassTest() throws IOException {
        final JavaType T = new JavaType.Simple("T");
        final JavaType iterable = new JavaType.Parameterized(new JavaType.Simple("Iterable"), List.of(T));
        final JavaType node = new JavaType.Parameterized(new JavaType.Simple("Node"), List.of(T));

        final MethodParameter first = new MethodParameter(node, "first");
        final MethodSignature defaultConstructor = new MethodSignature(0, null, "GenericWithInnerExample", List.of());
        final MethodSignature constructor = new MethodSignature(
                0,
                null,
                "GenericWithInnerExample",
                List.of(new MethodParameter(iterable, "iterable"))
        );
        final MethodSignature push = new MethodSignature(
                0,
                JavaType.Primitive.VOID,
                "push",
                List.of(new MethodParameter(T, "element"))
        );
        final MethodSignature iterator = new MethodSignature(
                Modifier.PUBLIC,
                new JavaType.Parameterized(new JavaType.Simple("Iterator"), List.of(T)),
                "iterator",
                List.of()
        );
        final SourcesContent content = content(Path.of("parser/type/GenericWithInnerExample.java"));
        assertEquals(
                new TypeDeclaration.ClassOrInterface(
                        0,
                        "GenericWithInnerExample",
                        null,
                        List.of(iterable),
                        List.of(first),
                        List.of(defaultConstructor, constructor, push, iterator)
                ),
                content.find("GenericWithInnerExample")
        );
    }

    @Test
    void recordTest() throws IOException {
        final MethodSignature constructor = new MethodSignature(
                0,
                null,
                "RecordExample",
                List.of()
        );
        final MethodSignature diff = new MethodSignature(
                0,
                JavaType.Primitive.INT,
                "diff",
                List.of()
        );
        final SourcesContent content = content(Path.of("parser/type/RecordExample.java"));
        assertEquals(
                new TypeDeclaration.ClassOrInterface(
                        0,
                        "RecordExample",
                        null,
                        List.of(),
                        List.of(),
                        List.of(constructor, diff)
                ),
                content.find("RecordExample")
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
