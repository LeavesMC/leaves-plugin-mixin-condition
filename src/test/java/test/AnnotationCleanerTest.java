package test;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leavesmc.plugin.mixin.condition.AnnotationCleaner;
import org.leavesmc.plugin.mixin.condition.annotations.Condition;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.objectweb.asm.Type.getDescriptor;

public class AnnotationCleanerTest {
    @Retention(RetentionPolicy.RUNTIME)
    @Condition(TestAnnotation1.Checker.class)
    public @interface TestAnnotation1 {
        class Checker {
            @SuppressWarnings("unused")
            boolean check(@NotNull AnnotationCleanerTest.TestAnnotation1 value) {
                return true;
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestAnnotation2 {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestAnnotation3 {
    }

    private AnnotationCleaner cleaner;

    @BeforeEach
    void setUp() {
        cleaner = new AnnotationCleaner(List.of(
            TestAnnotation2.class,
            TestAnnotation3.class
        ));
    }

    private @NotNull ClassNode createClassNodeWith(Class<?> annotationClass) {
        ClassNode cn = new ClassNode();
        cn.visibleAnnotations = new ArrayList<>(List.of(
            new AnnotationNode(getDescriptor(annotationClass))
        ));
        return cn;
    }

    @Test
    void testRemoveAnnotation() {
        ClassNode classNode = createClassNodeWith(TestAnnotation1.class);

        assertNotNull(classNode.visibleAnnotations);
        assertEquals(1, classNode.visibleAnnotations.size());
        assertEquals(getDescriptor(TestAnnotation1.class), classNode.visibleAnnotations.getFirst().desc);

        cleaner.onPreApply(classNode);

        classNode.visibleAnnotations.add(new AnnotationNode(getDescriptor(TestAnnotation3.class)));
        assertEquals(2, classNode.visibleAnnotations.size());

        cleaner.onPostApply(classNode);

        assertNotNull(classNode.visibleAnnotations);
        assertEquals(1, classNode.visibleAnnotations.size());
        assertEquals(getDescriptor(TestAnnotation1.class), classNode.visibleAnnotations.getFirst().desc);
    }

    @Test
    void testRestoreAnnotation() {
        ClassNode classNode = createClassNodeWith(TestAnnotation2.class);

        assertNotNull(classNode.visibleAnnotations);
        assertEquals(1, classNode.visibleAnnotations.size());
        assertEquals(getDescriptor(TestAnnotation2.class), classNode.visibleAnnotations.getFirst().desc);

        cleaner.onPreApply(classNode);

        classNode.visibleAnnotations.removeFirst();
        assertEquals(0, classNode.visibleAnnotations.size());

        cleaner.onPostApply(classNode);

        assertNotNull(classNode.visibleAnnotations);
        assertEquals(1, classNode.visibleAnnotations.size());
        assertEquals(getDescriptor(TestAnnotation2.class), classNode.visibleAnnotations.getFirst().desc);
    }

    @Test
    void testNoOperation() {
        ClassNode classNode = createClassNodeWith(TestAnnotation3.class);

        assertNotNull(classNode.visibleAnnotations);
        assertEquals(1, classNode.visibleAnnotations.size());
        assertEquals(getDescriptor(TestAnnotation3.class), classNode.visibleAnnotations.getFirst().desc);

        cleaner.onPreApply(classNode);

        assertEquals(1, classNode.visibleAnnotations.size());

        cleaner.onPostApply(classNode);

        assertNotNull(classNode.visibleAnnotations);
        assertEquals(1, classNode.visibleAnnotations.size());
        assertEquals(getDescriptor(TestAnnotation3.class), classNode.visibleAnnotations.getFirst().desc);
    }
}