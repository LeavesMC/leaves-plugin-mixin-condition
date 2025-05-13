# Mixin Condition

按照一定条件来确定是否启用 Leaves 插件中的某些 Mixin。

## 开始使用

> `leaves-plugin-mixin-condition` 仅可在支持 Leaves Plugin 及其 Mixin 特性的平台上可用。
>
> 目前还没有服务端正式发布此功能

我们建议直接使用 [Leaves Plugin Template](https://github.com/LeavesMC/leaves-plugin-template) 来创建插件，否则需要手动进行下面的步骤。

### 引入

```kts
repositories {
    maven("https://repo.leavesmc.org/releases/") {
        name = "leavesmc-releases"
    }
}

dependencies {
    compileOnly("org.leavesmc:leaves-plugin-template:1.0-SNAPSHOT")
}
```

### 集成

你需要创建一个 Mixin 配置插件 (`MixinConfigPlugin`) 以控制 Mixin 是否应用。有两种方法：

#### 最简单的办法

如果你完全不知道什么是 Mixin 配置插件，或者你不需要太多自定义逻辑，那么这种方法适合你。

```java
import org.leavesmc.plugin.mixin.condition.ConditionalMixinConfigPlugin;

public class TemplateMixinConfigPlugin extends ConditionalMixinConfigPlugin {
}
```

同时你需要在 Mixin 配置 Json 中添加此插件：

```json lines
{
    "plugin": "your-package.TemplateMixinConfigPlugin",
}
```

#### 通用方法

如果你已经编写了自定义的 Mixin 插件，并且不想让插件类继承其他类，那么可以参考我们的 [ConditionalMixinConfigPlugin](src/main/java/org/leavesmc/plugin/mixin/condition/ConditionalMixinConfigPlugin.java) 
的实现，自行添加 `ConditionChecker` 与 `AnnotationCleaner`。

### 控制 Mixin 类是否应用

#### 使用预设注解

完成了以上的步骤后，你可以像这样通过注解控制 Mixin 类是否应用：
```java
@Mixin(Main.class)
@ServerBuild(minecraft = "1.21.5", build = ">=114514")
public final class TemplateConditionalMixin {
    @Inject(method = "main", at = @At("HEAD"))
    private static void templateMixin(String[] args, CallbackInfo ci) {
        // ...
    }
}
```
所有可用的预设条件及其注解参考[这里](preset-conditions.md)。

**注意：如果你使用预设注解来控制 Mixin 类是否应用，那么只能同时存在一个注解类，否则运行时将抛出异常。**

例如，以下的做法是错误的：
```java
@Mixin(Main.class)
@MinecraftVersion("1.21.5")
@ServerBuild(minecraft = "1.21.5", build = ">=114514")
public final class TemplateConditionalMixin {
    @Inject(method = "main", at = @At("HEAD"))
    private static void templateMixin(String[] args, CallbackInfo ci) {
        // ...
    }
}
```

#### 使用自定义注解

如果你需要定义一些特殊的逻辑，那么自定义注解将提供最大的自由度。

例如，我们需要让一些 Mixin 类当 Minecraft 版本为 `1.19.x` **或**服务端构建为 `1.20.4@14` 的时候启用，则示例如下：
```java
import org.leavesmc.plugin.mixin.condition.BuildInfoProvider;
import org.leavesmc.plugin.mixin.condition.data.MinecraftVersionData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Condition(MyCondition.Checker.class)
public @interface MyCondition {
    class Checker {
        private static final MinecraftVersionData current = BuildInfoProvider.INSTANCE.getBuildInfo();

        @SuppressWarnings("unused")
        boolean check(MyCondition value) {
            MinecraftVersionData mcVersion = current.minecraftVersion();
            return (mcVersion.isGreaterThanOrEqualsTo("1.19") && mcVersion.isLessThan("1.20"))
                || (mcVersion.isEqualsTo("1.20.4") && current.buildNumber() == 14);
        }
    }
}
```
然后你可以在你的 Mixin 类上使用这个注解。同样的，它不能与其他条件注解同时使用。