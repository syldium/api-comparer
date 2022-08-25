package me.syldium.apicomparer.model.type;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record MethodSignature(int modifiers, @NotNull String name, @NotNull List<String> parameters) {
}
