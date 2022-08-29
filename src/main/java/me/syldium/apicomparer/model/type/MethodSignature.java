package me.syldium.apicomparer.model.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record MethodSignature(int modifiers, @Nullable JavaType returnType, @NotNull String name, @NotNull List<VariableDeclaration> parameters) {

    public boolean isConstructor() {
        return this.returnType == null;
    }
}
