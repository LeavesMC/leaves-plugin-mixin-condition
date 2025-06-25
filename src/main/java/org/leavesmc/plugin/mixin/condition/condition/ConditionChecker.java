package org.leavesmc.plugin.mixin.condition.condition;

import io.leangen.geantyref.AnnotationFormatException;
import io.leangen.geantyref.TypeFactory;
import org.jetbrains.annotations.NotNull;
import org.leavesmc.plugin.mixin.condition.annotations.Condition;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConditionChecker {
    private static ClassLoader loader = ConditionChecker.class.getClassLoader();

    public static boolean shouldApplyMixin(String mixinClassName) {
        ClassNode mixinClass = getClassNode(mixinClassName);
        List<AnnotationNode> annotations = getAnnotations(mixinClass).stream()
            .filter(ConditionChecker::isCondition)
            .toList();
        if (annotations.size() > 1) throw new IllegalArgumentException(
            "Only one condition annotation can be provided, but " + annotations.size() + " was found"
        );
        if (annotations.isEmpty()) return true;
        return checkCondition(annotations.getFirst());
    }

    public static void setClassLoader(ClassLoader loader) {
        ConditionChecker.loader = loader;
    }

    private static @NotNull String parseClassName(@NotNull String className) {
        if (className.startsWith("L") && className.endsWith(";")) {
            return className.substring(1, className.length() - 1).replace('/', '.');
        }
        return className.replace('/', '.');
    }

    private static @NotNull ClassNode getClassNode(String className) {
        try {
            return MixinService.getService().getBytecodeProvider().getClassNode(className);
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static @NotNull Class<?> getClass(@NotNull String className) {
        return getClass(getClassNode(className));
    }

    private static @NotNull Class<?> getClass(@NotNull ClassNode classNode) {
        String className = parseClassName(classNode.name);
        try {
            return loader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + className, e);
        }
    }

    private static @NotNull List<AnnotationNode> getAnnotations(@NotNull ClassNode classNode) {
        List<AnnotationNode> annotations = new ArrayList<>();
        if (classNode.visibleAnnotations != null) {
            annotations.addAll(classNode.visibleAnnotations);
        }
        return annotations;
    }

    private static @NotNull ClassNode getAnnotationClass(@NotNull AnnotationNode annotationNode) {
        String desc = annotationNode.desc;
        String className = parseClassName(desc);
        return getClassNode(className);
    }

    private static AnnotationNode getConditionAnnotation(@NotNull ClassNode annotationClass) {
        return Annotations.getVisible(annotationClass, Condition.class);
    }

    private static boolean isCondition(@NotNull AnnotationNode annotationNode) {
        ClassNode annotationType = getAnnotationClass(annotationNode);
        return getConditionAnnotation(annotationType) != null;
    }

    private static Annotation getAnnotationInstance(@NotNull AnnotationNode annotationNode) {
        ClassNode annotationClassNode = getAnnotationClass(annotationNode);
        @SuppressWarnings("unchecked")
        Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) getClass(annotationClassNode);
        List<Object> values = annotationNode.values;
        Map<String, Object> data = IntStream.range(0, values.size() / 2)
            .boxed()
            .collect(Collectors.toMap(
                i -> (String) values.get(i * 2),
                i -> values.get(i * 2 + 1)
            ));
        try {
            return TypeFactory.annotation(annotationClass, data);
        } catch (AnnotationFormatException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean checkCondition(@NotNull AnnotationNode annotationNode) {
        ClassNode annotationClassNode = getAnnotationClass(annotationNode);
        Class<?> annotationClass = getClass(annotationClassNode);
        AnnotationNode conditionAnnotation = getConditionAnnotation(annotationClassNode);
        Type checkerType = Annotations.getValue(conditionAnnotation, "value");
        Class<?> checkerClass = getClass(checkerType.getClassName());
        Annotation conditionAnnotationInstance = getAnnotationInstance(annotationNode);
        try {
            Object checker = checkerClass.getDeclaredConstructor().newInstance();
            Method checkMethod = checkerClass.getDeclaredMethod("check", annotationClass);
            checkMethod.setAccessible(true);
            return (boolean) checkMethod.invoke(checker, conditionAnnotationInstance);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
