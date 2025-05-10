package org.leavesmc.plugin.mixin.condition;

import org.jetbrains.annotations.NotNull;
import org.leavesmc.plugin.mixin.condition.annotation.Condition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ConditionChecker {
    public boolean shouldApplyMixin(String mixinClassName) {
        Class<?> mixinClass = getClass(mixinClassName);
        List<Annotation> conditionAnnotations = Arrays.stream(mixinClass.getAnnotations())
            .filter(ConditionChecker::isCondition)
            .toList();
        if (conditionAnnotations.size() > 1) throw new IllegalArgumentException(
            "Only one condition annotation can be provided, but " + conditionAnnotations.size() + " was found"
        );
        if (conditionAnnotations.isEmpty()) return true;
        return checkCondition(conditionAnnotations.getFirst());
    }

    private @NotNull Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isCondition(@NotNull Annotation annotation) {
        Condition condition = annotation.annotationType().getAnnotation(Condition.class);
        return condition != null;
    }

    private boolean checkCondition(@NotNull Annotation annotation) {
        Class<?> annotationType = annotation.annotationType();
        Condition checkerAnnotation = annotationType.getAnnotation(Condition.class);
        Class<?> checkerClass = checkerAnnotation.value();
        try {
            Object checker = checkerClass.getDeclaredConstructor().newInstance();
            Method checkMethod = checkerClass.getDeclaredMethod("check", annotationType);
            checkMethod.setAccessible(true);
            return (boolean) checkMethod.invoke(checker, annotation);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
