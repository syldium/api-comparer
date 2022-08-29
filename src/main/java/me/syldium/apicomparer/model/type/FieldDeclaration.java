package me.syldium.apicomparer.model.type;

import org.jetbrains.annotations.NotNull;

public record FieldDeclaration(int modifiers, @NotNull VariableDeclaration variable) {
}
