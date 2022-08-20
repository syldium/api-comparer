package me.syldium.apicomparer.model;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public record SourcesContent(@NotNull Set<String> files) {

    /**
     * Compare the sources with another version.
     *
     * @param other the other sources
     * @return the differences found between the two versions
     */
    public @NotNull VersionDiff diff(@NotNull SourcesContent other) {
        final Set<String> added = new HashSet<>();
        final Set<String> removed = new HashSet<>();
        for (String otherClass : other.files) {
            if (!this.files.contains(otherClass)) {
                added.add(otherClass);
            }
        }
        for (String selfClass : this.files) {
            if (!other.files.contains(selfClass)) {
                removed.add(selfClass);
            }
        }
        return new VersionDiff(added, removed);
    }
}
