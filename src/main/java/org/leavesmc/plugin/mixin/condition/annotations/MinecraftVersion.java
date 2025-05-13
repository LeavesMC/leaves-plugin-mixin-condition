package org.leavesmc.plugin.mixin.condition.annotations;

import org.jetbrains.annotations.NotNull;
import org.leavesmc.plugin.mixin.condition.BuildInfoProvider;
import org.leavesmc.plugin.mixin.condition.condition.ComparableExpression;
import org.leavesmc.plugin.mixin.condition.data.MinecraftVersionData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Condition(MinecraftVersion.Checker.class)
public @interface MinecraftVersion {
    String value();

    class Checker {
        private static final MinecraftVersionData current = BuildInfoProvider.INSTANCE.getBuildInfo().minecraftVersion();

        @SuppressWarnings("unused")
        boolean check(@NotNull MinecraftVersion value) {
            ComparableExpression<MinecraftVersionData> condition = ComparableExpression.parse(
                value.value(),
                MinecraftVersionData::fromString
            );
            return condition.matches(current);
        }
    }
}
