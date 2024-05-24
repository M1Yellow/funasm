## 说明

使用 `Javaagent` + `ASM` 实现动态修改类方法。ASM 使用 JDK 自带的版本，无需引入其他依赖，大幅减小 jar 包文件大小。

可应用在 IDEA 30 天试用到期后，自动去掉 Licenses（许可证）弹窗，达到【无限试用】的效果。（改系统时间，或者先试用，再移除试用许可证，不用真等 30 天）

目前仅短期测试了 `IDEA 2023.3.6`、`IDEA 2024.1.1` 版本正常使用，其他开发工具/版本请自行修改测试。

本项目**仅供技术参考学习**！有条件的**请支持正版**或申请优惠渠道！



<br/>

## 使用

1. 官网下载对应版本的 IDEA，建议下载压缩包版本
2. 下载部署 Oracle JDK 17 / Open JDK 17 环境
3. 下载配置 Maven 3.6.3 （或更高版本）
4. `git clone url` 克隆项目到本地
5. IDEA 打开项目（打开 doidea 文件夹即可），Maven package 打包
6. target 目录下的 `doidea-asm-1.0.0.jar` 为可用 jar 包，复制到一个目录，比如：`E:\DevRes\doidea`
7. `idea64.exe.vmoptions` 添加以下 3 条配置，即可生效
   1. -javaagent:E:\DevRes\doidea\doidea-asm-1.0.0.jar
   2. --add-opens=java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED
   3. --add-opens=java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED


<br/>

> - Launcher.java 中 `import jdk.internal.org.objectweb.asm.*;` 报错，但不影响 Maven package 打包
> - 在 `import jdk.internal.org.objectweb.asm.*;` 这一行，按 `Alt + Enter`，选择 `--add-exports java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED` 添加到模块编译器选项，即可不报错
> - 也可以到 `文件-设置-构建、执行、部署-编译器-Java编译器` 最下方的 `模块-编译选项` 中手动配置



<br/>

## 参考

- [Jexbraxxs系列破解思路的详细实现步骤探索](https://www.52pojie.cn/thread-1921814-1-1.html)
- [JexBraxxs 全家桶系列 2024 破解思路](https://www.52pojie.cn/thread-1919098-1-1.html)
- [Java ASM系列](https://lsieun.github.io/java/asm/)
- [javaagent+asm破解cexxum](https://www.cnblogs.com/alinainai/p/12188496.html)



<br/>
