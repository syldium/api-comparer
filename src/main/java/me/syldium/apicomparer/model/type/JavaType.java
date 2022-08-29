package me.syldium.apicomparer.model.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public sealed interface JavaType permits JavaType.Array, JavaType.Simple, JavaType.Parameterized, JavaType.Primitive, JavaType.Wildcard {

    record Array(@NotNull JavaType element, int depth) implements JavaType {
    }

    record Simple(@NotNull String fqcn) implements JavaType {
    }

    record Parameterized(@NotNull JavaType of, @NotNull List<JavaType> parameters) implements JavaType {
    }

    enum Primitive implements JavaType {
        BYTE,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        BOOLEAN,
        CHAR,
        VOID
    }

    record Wildcard(@Nullable Bound bound) implements JavaType {

        public record Bound(@NotNull Constraint constraint, @NotNull JavaType type) {
        }
        public enum Constraint {
            EXTENDS,
            SUPER
        }
    }
}
