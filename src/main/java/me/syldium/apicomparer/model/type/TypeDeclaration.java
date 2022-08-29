package me.syldium.apicomparer.model.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represent a Java type.
 */
public sealed interface TypeDeclaration permits TypeDeclaration.ClassOrInterface, TypeDeclaration.Enum, TypeDeclaration.Record {

    int modifiers();

    @NotNull String name();

    record ClassOrInterface(int modifiers, @NotNull String name,
                            @Nullable JavaType superType,
                            @NotNull List<JavaType> superInterfaces,
                            @NotNull List<FieldDeclaration> fields,
                            @NotNull List<MethodSignature> methods) implements TypeDeclaration {
    }

    record Enum(int modifiers, @NotNull String name,
                @NotNull List<String> constants,
                @NotNull List<FieldDeclaration> fields,
                @NotNull List<MethodSignature> methods) implements TypeDeclaration {
    }

    record Record(int modifiers, @NotNull String name,
                  @NotNull List<VariableDeclaration> components,
                  @NotNull List<JavaType> superInterfaces,
                  @NotNull List<FieldDeclaration> fields,
                  @NotNull List<MethodSignature> methods) implements TypeDeclaration {
    }
}
