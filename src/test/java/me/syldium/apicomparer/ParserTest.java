package me.syldium.apicomparer;

import me.syldium.apicomparer.io.SourcesCollector;
import me.syldium.apicomparer.model.SourcesContent;
import me.syldium.apicomparer.model.type.FieldDeclaration;
import me.syldium.apicomparer.model.type.JavaType;
import me.syldium.apicomparer.model.type.TypeParameter;
import me.syldium.apicomparer.model.type.VariableDeclaration;
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
                List.of(new VariableDeclaration(new JavaType.Array(STRING_TYPE, 1), "args"))
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

    @Test
    void enumTest() throws IOException {
        final JavaType enumExampleType = new JavaType.Simple("EnumExample");
        final MethodSignature from = new MethodSignature(
                Modifier.STATIC,
                enumExampleType,
                "from",
                List.of(new VariableDeclaration(JavaType.Primitive.BOOLEAN, "value"))
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
                        List.of(new FieldDeclaration(Modifier.STATIC | Modifier.FINAL, new VariableDeclaration(JavaType.Primitive.INT, "VALUES_LEN"))),
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

        final FieldDeclaration first = new FieldDeclaration(Modifier.PRIVATE, new VariableDeclaration(node, "first"));
        final MethodSignature defaultConstructor = new MethodSignature(0, null, "GenericWithInnerExample", List.of());
        final MethodSignature constructor = new MethodSignature(
                0,
                null,
                "GenericWithInnerExample",
                List.of(new VariableDeclaration(iterable, "iterable"))
        );
        final MethodSignature push = new MethodSignature(
                0,
                JavaType.Primitive.VOID,
                "push",
                List.of(new VariableDeclaration(T, "element"))
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
                        List.of(new TypeParameter("T")),
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
        final FieldDeclaration zero = new FieldDeclaration(
                Modifier.STATIC | Modifier.FINAL,
                new VariableDeclaration(new JavaType.Simple("RecordExample"), "ZERO")
        );
        final MethodSignature diff = new MethodSignature(
                0,
                JavaType.Primitive.INT,
                "diff",
                List.of()
        );
        final SourcesContent content = content(Path.of("parser/type/RecordExample.java"));
        assertEquals(
                new TypeDeclaration.Record(
                        0,
                        "RecordExample",
                        List.of(
                                new VariableDeclaration(JavaType.Primitive.INT, "min"),
                                new VariableDeclaration(JavaType.Primitive.INT, "max")
                        ),
                        List.of(zero),
                        List.of(constructor, diff)
                ),
                content.find("RecordExample")
        );
    }

    @Test
    void throwTest() throws IOException {
        final MethodSignature createFile = new MethodSignature(
                0,
                List.of(),
                JavaType.Primitive.VOID,
                "createFile",
                List.of(),
                List.of(new JavaType.Simple("IOException"))
        );
        final SourcesContent content = content(Path.of("parser/type/ThrowExample.java"));
        assertEquals(
                new TypeDeclaration.ClassOrInterface(
                        Modifier.FINAL,
                        "ThrowExample",
                        List.of(),
                        List.of(createFile)
                ),
                content.find("ThrowExample")
        );
    }

    @Test
    void wildcardTest() throws IOException {
        final JavaType T = new JavaType.Simple("T");
        final MethodSignature apply = new MethodSignature(
                Modifier.STATIC,
                List.of(new TypeParameter("T")),
                JavaType.Primitive.VOID,
                "apply",
                List.of(
                        new VariableDeclaration(
                                new JavaType.Parameterized(
                                        new JavaType.Simple("Iterable"),
                                        List.of(new JavaType.Wildcard(new JavaType.Wildcard.Bound(JavaType.Wildcard.Constraint.EXTENDS, T)))
                                ),
                                "input"
                        ),
                        new VariableDeclaration(
                                new JavaType.Parameterized(
                                        new JavaType.Simple("Consumer"),
                                        List.of(new JavaType.Wildcard(new JavaType.Wildcard.Bound(JavaType.Wildcard.Constraint.SUPER, T)))
                                ),
                                "consumer"
                        )
                ),
                List.of()
        );
        final SourcesContent content = content(Path.of("parser/type/WildcardExample.java"));
        assertEquals(
                new TypeDeclaration.ClassOrInterface(
                        Modifier.FINAL,
                        "WildcardExample",
                        List.of(),
                        List.of(apply)
                ),
                content.find("WildcardExample")
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
