package me.syldium.apicomparer.model;

import me.syldium.apicomparer.model.type.TypeDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SourcesContent {

    private final Map<String, TypeDeclaration> types = new HashMap<>();

    public @Nullable TypeDeclaration find(@NotNull String fqdn) {
        return this.types.get(fqdn);
    }

    public void register(@NotNull TypeDeclaration type) {
        this.types.put(type.name(), type);
    }

    /**
     * Compare the sources with another version.
     *
     * @param other the other sources
     * @return the differences found between the two versions
     */
    public @NotNull VersionDiff diff(@NotNull SourcesContent other) {
        final Set<TypeDeclaration> added = new HashSet<>();
        final Set<TypeDeclaration> changed = new HashSet<>();
        final Set<TypeDeclaration> removed = new HashSet<>();
        for (TypeDeclaration otherType : other.types.values()) {
            final TypeDeclaration selfType = find(otherType.name());
            if (selfType == null) {
                added.add(otherType);
            } else if (!selfType.equals(otherType)) {
                changed.add(otherType);
            }
        }
        for (TypeDeclaration selfType : this.types.values()) {
            if (other.find(selfType.name()) == null) {
                removed.add(selfType);
            }
        }
        return new VersionDiff(added, changed, removed);
    }
}
