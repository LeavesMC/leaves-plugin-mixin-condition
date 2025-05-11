package org.leavesmc.plugin.mixin.condition.condition;

import org.jetbrains.annotations.NotNull;
import org.leavesmc.plugin.mixin.condition.annotations.Condition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ConditionChecker {
    public static boolean shouldApplyMixin(String mixinClassName) {
        Class<?> mixinClass = getClass(mixinClassName);
        List<Annotation> annotations = Arrays.stream(mixinClass.getAnnotations())
            .filter(ConditionChecker::isCondition)
            .toList();
        if (annotations.size() > 1) throw new IllegalArgumentException(
            "Only one condition annotation can be provided, but " + annotations.size() + " was found"
        );
        if (annotations.isEmpty()) return true;
        return checkCondition(annotations.getFirst());
    }

    private static @NotNull Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isCondition(@NotNull Annotation annotation) {
        Class<?> annotationType = annotation.annotationType();
        Condition condition = annotationType.getAnnotation(Condition.class);
        return condition != null;
    }

    private static boolean checkCondition(@NotNull Annotation annotation) {
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
