package me.syldium.apicomparer.model.type;

import org.jetbrains.annotations.NotNull;

public record MethodParameter(@NotNull JavaType type, @NotNull String name) {
}
