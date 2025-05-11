package org.leavesmc.plugin.mixin.condition;

import org.jetbrains.annotations.NotNull;
import org.leavesmc.plugin.mixin.condition.annotations.MinecraftVersion;
import org.leavesmc.plugin.mixin.condition.annotations.ServerBuild;
import org.leavesmc.plugin.mixin.condition.condition.ConditionChecker;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public abstract class ConditionalMixinConfigPlugin implements IMixinConfigPlugin {
    private final AnnotationCleaner annotationCleaner = new AnnotationCleaner(getAnnotations());

    protected List<Class<? extends Annotation>> getExtraAnnotations() {
        return List.of();
    }

    protected final @NotNull List<Class<? extends Annotation>> getAnnotations() {
        List<Class<? extends Annotation>> result = getExtraAnnotations();
        result.addAll(List.of(
            MinecraftVersion.class,
            ServerBuild.class
        ));
        return result;
    }

    protected final void registerMixinGroup(String name) {

    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return ConditionChecker.shouldApplyMixin(mixinClassName);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        annotationCleaner.onPreApply(targetClass);
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        annotationCleaner.onPostApply(targetClass);
    }
}
