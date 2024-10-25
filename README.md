安装信息
=======

此模板存储库可以直接克隆，以便您开始使用新的
国防部。只需创建一个从此仓库克隆的新仓库，按照
[GitHub]（https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-repository-from-a-template） 上的说明。

获得克隆后，只需在您选择的 IDE 中打开存储库即可。IDE 的通常建议是 IntelliJ IDEA 或 Eclipse。

> **注意**：对于 Eclipse，请使用 'Launch Group' 中的任务，而不是在 'Java Application' 中找到的任务。在启动游戏之前，必须运行准备任务。NeoGradle 随后使用 launch groups 来执行这些操作。

如果您在任何时候在 IDE 中缺少库，或者遇到问题，您可以
运行 'gradlew --refresh-dependencies' 刷新本地缓存。'gradlew clean' 重置所有内容
{这不会影响您的代码}，然后再次启动该进程。

映射名称：
============
默认情况下，MDK 配置为使用 Mojang 的官方映射名称作为方法和字段
在 Minecraft 代码库中。这些名称由特定许可证涵盖。所有 Mod 作者都应该意识到这一点
许可证。有关最新的许可证文本，请参阅映射文件本身，或在此处查看参考副本：
https://github.com/NeoForged/NeoForm/blob/main/Mojang.md

其他资源：
==========
社区文档： https://docs.neoforged.net/ 
NeoForged Discord：https://discord.neoforged.net/