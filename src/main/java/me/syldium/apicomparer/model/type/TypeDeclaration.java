package me.syldium.apicomparer.model.type;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represent a Java type.
 */
public record TypeDeclaration(@NotNull String name, @NotNull List<String> fields, @NotNull List<String> methods) {
}
