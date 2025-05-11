package org.leavesmc.plugin.mixin.condition.annotations;

import org.jetbrains.annotations.NotNull;
import org.leavesmc.plugin.mixin.condition.condition.ComparableCondition;
import org.leavesmc.plugin.mixin.condition.data.BuildInfo;
import org.leavesmc.plugin.mixin.condition.BuildInfoProvider;
import org.leavesmc.plugin.mixin.condition.data.MinecraftVersionData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.leavesmc.plugin.mixin.condition.data.MinecraftVersionData.minecraftVersion;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Condition(ServerBuild.Checker.class)
public @interface ServerBuild {
    String minecraft();
    String build();

    class Checker {
        private static final BuildInfo current = BuildInfoProvider.INSTANCE.getBuildInfo();

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
