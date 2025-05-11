package org.leavesmc.plugin.mixin.condition;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.util.Annotations;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.IntStream;

// 这里的实现思路是模仿的 https://github.com/Fallen-Breath/conditional-mixin/blob/master/common/src/main/java/me/fallenbreath/conditionalmixin/impl/AnnotationCleanerImpl.java
// 但具体实现是重写的，我认为应该不需要使用同样的 LGPL-3.0 协议
public class AnnotationCleaner {
    private final List<Class<? extends Annotation>> annotationClasses;
    private final Map<Class<? extends Annotation>, AnnotationNode> previousRestrictionAnnotations = new HashMap<>();

    public AnnotationCleaner(List<Class<? extends Annotation>> annotationClasses) {
        this.annotationClasses = annotationClasses;
    }

    public void onPreApply(ClassNode targetClass) {
        this.previousRestrictionAnnotations.clear();
        this.annotationClasses.forEach(annotationClass ->
            previousRestrictionAnnotations.put(
                annotationClass,
                Annotations.getVisible(targetClass, annotationClass)
            )
        );
    }

    public void onPostApply(@NotNull ClassNode targetClass) {
        Optional.ofNullable(targetClass.visibleAnnotations)
            .ifPresent(annotations ->
                annotationClasses.forEach(annotationClass ->
                    processAnnotation(annotations, annotationClass)
                )
            );
    }

    private void processAnnotation(List<AnnotationNode> annotations, Class<?> annotationClass) {
        String descriptor = Type.getDescriptor(annotationClass);

        int index = findAnnotationIndex(annotations, descriptor);
        if (index == -1) return;

        AnnotationNode previous = previousRestrictionAnnotations.get(annotationClass);
        updateAnnotation(annotations, index, previous);
    }

    private int findAnnotationIndex(@NotNull List<AnnotationNode> annotations, String descriptor) {
        return IntStream.range(0, annotations.size())
            .filter(i -> descriptor.equals(annotations.get(i).desc))
            .findFirst()
            .orElse(-1);
    }

    private void updateAnnotation(@NotNull List<AnnotationNode> annotations, int index, AnnotationNode previous) {
        if (previous == null) {
            annotations.remove(index);
        } else {
            annotations.set(index, previous);
        }
    }
}