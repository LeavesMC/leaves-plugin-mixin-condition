# 预设条件

> 关于表达式：
> 
> 目前本库只有一种表达式：可比较表达式。
>
> 这种表达式基于比较构建，需要目标类实现 `Comparable`。
>
> 可用的表达方式以及格式需要参考 [ComparableExpressionTest](./src/test/java/test/condition/ComparableExpressionTest.java)。

## Minecraft 版本

`@MinecraftVersion`

| 参数名   | 描述              |
|-------|-----------------|
| value | Minecraft 版本表达式 |

## 服务端构建

`@ServerBuild`

| 参数名       | 描述            |
|-----------|---------------|
| minecraft | 单个Minecraft版本 |
| build     | 服务端构建号表达式     |