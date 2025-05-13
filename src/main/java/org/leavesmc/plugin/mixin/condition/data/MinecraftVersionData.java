package org.leavesmc.plugin.mixin.condition.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record MinecraftVersionData(
    int major,
    int minor,
    int patch
) implements Comparable<MinecraftVersionData> {
    @Contract("_ -> new")
    public static @NotNull MinecraftVersionData fromString(@NotNull String version) {
        List<Integer> split = Arrays.stream(version.split("\\."))
            .map(Integer::parseInt)
            .collect(Collectors.toList());
        switch (split.size()) {
            case 2: split.add(0);
            case 3: return new MinecraftVersionData(split.get(0), split.get(1), split.get(2));
            default: throw new IllegalArgumentException("Invalid version: " + version);
        }
    }

    public static @NotNull MinecraftVersionData minecraftVersion(@NotNull String version) {
        return fromString(version);
    }

    public static @NotNull MinecraftVersionData minecraftVersion(int major, int minor, int patch) {
        return new MinecraftVersionData(major, minor, patch);
    }

    @Override
    public int compareTo(@NotNull MinecraftVersionData other) {
        int c1 = Integer.compare(this.major, other.major);
        if (c1 != 0) return c1;
        int c2 = Integer.compare(this.minor, other.minor);
        if (c2 != 0) return c2;
        return Integer.compare(this.patch, other.patch);
    }

    public boolean isGreaterThan(MinecraftVersionData other) {
        return this.compareTo(other) > 0;
    }

    public boolean isGreaterThan(String other) {
        return this.isGreaterThan(minecraftVersion(other));
    }

    public boolean isGreaterThanOrEqualTo(MinecraftVersionData other) {
        return this.compareTo(other) >= 0;
    }

    public boolean isGreaterThanOrEqualTo(String other) {
        return this.isGreaterThanOrEqualTo(minecraftVersion(other));
    }

    public boolean isLessThan(MinecraftVersionData other) {
        return this.compareTo(other) < 0;
    }

    public boolean isLessThan(String other) {
        return this.isLessThan(minecraftVersion(other));
    }

    public boolean isLessThanOrEqualTo(MinecraftVersionData other) {
        return this.compareTo(other) <= 0;
    }

    public boolean isLessThanOrEqualTo(String other) {
        return this.isLessThanOrEqualTo(minecraftVersion(other));
    }

    public boolean isEqualTo(MinecraftVersionData other) {
        return this.compareTo(other) == 0;
    }

    public boolean isEqualTo(String other) {
        return this.isEqualTo(minecraftVersion(other));
    }
}
