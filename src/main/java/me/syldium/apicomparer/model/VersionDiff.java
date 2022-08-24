package me.syldium.apicomparer.model;

import me.syldium.apicomparer.model.type.TypeDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * The differences found between two versions of the sources.
 *
 * @param addedTypes types added in the second version that were not present in the first version
 * @param changedTypes types changed between the two versions
 * @param removedTypes types removed in the second version that were present in the first version
 */
public record VersionDiff(@NotNull Set<TypeDeclaration> addedTypes, @NotNull Set<TypeDeclaration> changedTypes, @NotNull Set<TypeDeclaration> removedTypes) {
}
