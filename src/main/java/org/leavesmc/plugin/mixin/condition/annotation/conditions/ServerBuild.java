package org.leavesmc.plugin.mixin.condition.annotation.conditions;

import org.jetbrains.annotations.NotNull;
import org.leavesmc.plugin.mixin.condition.ComparableCondition;
import org.leavesmc.plugin.mixin.condition.annotation.Condition;
import org.leavesmc.plugin.mixin.condition.build.BuildInfo;
import org.leavesmc.plugin.mixin.condition.build.BuildInfoManager;
import org.leavesmc.plugin.mixin.condition.data.MinecraftVersionData;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.leavesmc.plugin.mixin.condition.data.MinecraftVersionData.minecraftVersion;

@Retention(RetentionPolicy.RUNTIME)
@Condition(ServerBuild.Checker.class)
public @interface ServerBuild {
    String minecraft();
    String build();

    class Checker {
        private static final BuildInfo current = BuildInfoManager.INSTANCE.getBuildInfo();

        @SuppressWarnings("unused")
        boolean check(@NotNull ServerBuild value) {
            MinecraftVersionData target = minecraftVersion(value.minecraft());
            if (!target.isEqualTo(current.minecraftVersion())) return false;
            ComparableCondition<Integer> buildCondition = ComparableCondition.parse(
                value.build(),
                Integer::parseInt
            );
            return buildCondition.matches(current.buildNumber());
        }
    }
}
