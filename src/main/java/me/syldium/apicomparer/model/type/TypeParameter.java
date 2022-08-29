package me.syldium.apicomparer.model.type;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record TypeParameter(@NotNull String name, @NotNull List<JavaType> bounds) {

    public TypeParameter(@NotNull String name) {
        this(name, List.of());
    }
}
