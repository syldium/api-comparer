package me.syldium.apicomparer.model;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * The differences found between two versions of the sources.
 *
 * @param addedFiles files added in the second version that were not present in the first version
 * @param removedFiles files removed in the second version that were present in the first version
 */
public record VersionDiff(@NotNull Set<String> addedFiles, @NotNull Set<String> removedFiles) {
}
