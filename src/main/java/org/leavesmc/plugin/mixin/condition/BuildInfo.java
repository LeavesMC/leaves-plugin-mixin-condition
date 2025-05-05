package org.leavesmc.plugin.mixin.condition;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static org.leavesmc.plugin.mixin.condition.MinecraftVersion.minecraftVersion;

public record BuildInfo(
    String projectName,
    MinecraftVersion mcVersion,
    int buildNumber
) {
    @Contract("_ -> new")
    public static @NotNull BuildInfo fromString(@NotNull String buildInfoString) {
        String[] split = buildInfoString.split("\t");
        if (split.length != 3) {
            throw new IllegalArgumentException("Invalid build info string: " + buildInfoString);
        }
        return new BuildInfo(
            split[0],
            minecraftVersion(split[1]),
            Integer.parseInt(split[2])
        );
    }
}
