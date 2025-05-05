package org.leavesmc.plugin.mixin.condition;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record MinecraftVersion(
    int major,
    int minor,
    int patch
) implements Comparable<MinecraftVersion> {
    @Contract("_ -> new")
    public static @NotNull MinecraftVersion fromString(@NotNull String version) {
        List<Integer> split = Arrays.stream(version.split("\\."))
            .map(Integer::parseInt)
            .collect(Collectors.toList());
        switch (split.size()) {
            case 2: split.add(0);
            case 3: return new MinecraftVersion(split.get(0), split.get(1), split.get(2));
            default: throw new IllegalArgumentException("Invalid version: " + version);
        }
    }

    public static @NotNull MinecraftVersion minecraftVersion(@NotNull String version) {
        return fromString(version);
    }

    public static @NotNull MinecraftVersion minecraftVersion(int major, int minor, int patch) {
        return new MinecraftVersion(major, minor, patch);
    }

    @Override
    public int compareTo(@NotNull MinecraftVersion other) {
        int c1 = Integer.compare(this.major, other.major);
        if (c1 != 0) return c1;
        int c2 = Integer.compare(this.minor, other.minor);
        if (c2 != 0) return c2;
        return Integer.compare(this.patch, other.patch);
    }

    public boolean isGreaterThan(MinecraftVersion other) {
        return this.compareTo(other) > 0;
    }

    public boolean isGreaterThanOrEqualTo(MinecraftVersion other) {
        return this.compareTo(other) >= 0;
    }

    public boolean isLessThan(MinecraftVersion other) {
        return this.compareTo(other) < 0;
    }

    public boolean isLessThanOrEqualTo(MinecraftVersion other) {
        return this.compareTo(other) <= 0;
    }

    public boolean isEqualTo(MinecraftVersion other) {
        return this.compareTo(other) == 0;
    }
}
