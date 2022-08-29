package me.syldium.apicomparer.model.type;

import org.jetbrains.annotations.NotNull;

public record VariableDeclaration(@NotNull JavaType type, @NotNull String name) {
}
