package me.syldium.apicomparer.model.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record MethodSignature(int modifiers,
                              @NotNull List<TypeParameter> typeParameters,
                              @Nullable JavaType returnType,
                              @NotNull String name,
                              @NotNull List<VariableDeclaration> parameters,
                              @NotNull List<JavaType> thrownExceptions) {

    public MethodSignature(int modifiers, @Nullable JavaType returnType, @NotNull String name, @NotNull List<VariableDeclaration> parameters) {
        this(modifiers, List.of(), returnType, name, parameters, List.of());
    }

    public boolean isConstructor() {
        return this.returnType == null;
    }
}
