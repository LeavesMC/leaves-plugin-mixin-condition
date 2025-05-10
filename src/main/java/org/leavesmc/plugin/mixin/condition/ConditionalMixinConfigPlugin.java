package org.leavesmc.plugin.mixin.condition;

import org.leavesmc.plugin.mixin.condition.annotation.AnnotationCleaner;
import org.leavesmc.plugin.mixin.condition.annotation.conditions.MinecraftVersion;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;


@SuppressWarnings("unused")
public abstract class ConditionalMixinConfigPlugin implements IMixinConfigPlugin {
    private final ConditionChecker conditionChecker = new ConditionChecker();
    private final AnnotationCleaner annotationCleaner = new AnnotationCleaner(
        MinecraftVersion.class
    );

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return conditionChecker.shouldApplyMixin(mixinClassName);
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
