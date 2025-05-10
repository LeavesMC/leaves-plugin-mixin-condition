package org.leavesmc.plugin.mixin.condition.annotation.conditions;

import org.jetbrains.annotations.NotNull;
import org.leavesmc.plugin.mixin.condition.annotation.Condition;
import org.leavesmc.plugin.mixin.condition.build.BuildInfoManager;
import org.leavesmc.plugin.mixin.condition.ComparableCondition;
import org.leavesmc.plugin.mixin.condition.data.MinecraftVersionData;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Condition(MinecraftVersion.Checker.class)
public @interface MinecraftVersion {
    String value();

    class Checker {
        private static final MinecraftVersionData current = BuildInfoManager.INSTANCE.getBuildInfo().minecraftVersion();

        @SuppressWarnings("unused")
        boolean check(@NotNull MinecraftVersion value) {
            ComparableCondition<MinecraftVersionData> condition = ComparableCondition.parse(
                value.value(),
                MinecraftVersionData::fromString
            );
            return condition.matches(current);
        }
    }
}
