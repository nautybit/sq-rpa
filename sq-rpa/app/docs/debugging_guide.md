# 松鼠RPA调试指南 - Redmi 9A & 微信8.0.42

本文档提供在Redmi 9A设备上运行和调试松鼠RPA项目的详细步骤，特别针对微信8.0.42版本进行适配。

## 目录

1. [开发环境准备](#开发环境准备)
2. [设备准备](#设备准备)
3. [项目构建](#项目构建)
4. [部署到设备](#部署到设备)
5. [辅助功能配置](#辅助功能配置)
6. [微信元素适配](#微信元素适配)
7. [常见问题解决](#常见问题解决)
8. [高级调试技巧](#高级调试技巧)

## 开发环境准备

### 必要工具

1. **Android Studio**: 下载并安装最新版本的Android Studio
   - 推荐版本: Android Studio Giraffe (2023.1.1)或更高版本
   - 下载地址: https://developer.android.com/studio

2. **JDK**: 确保安装了JDK 17
   - Android Studio通常会自带OpenJDK

3. **Git**: 用于版本控制
   - Windows: https://git-scm.com/download/win
   - Mac: `brew install git`

### 克隆项目

```bash
git clone https://github.com/yourusername/sq-rpa.git
cd sq-rpa
```

## 设备准备

### 1. 启用开发者选项

1. 打开Redmi 9A的**设置**
2. 进入**关于手机**
3. 连续点击**MIUI版本**7次，直到看到"您已处于开发者模式"提示
4. 返回**设置**，进入**更多设置** > **开发者选项**
5. 启用**USB调试**

### 2. 安装必要驱动

- 如果您使用Windows，可能需要安装小米USB驱动
- 驱动下载地址: https://developer.android.com/studio/run/oem-usb

### 3. 允许调试

1. 使用USB数据线连接Redmi 9A和电脑
2. 手机上会弹出USB调试授权对话框，点击**允许**
3. 勾选**始终允许从此计算机进行调试**选项

### 4. 设置微信

1. 确保已安装微信8.0.42版本
2. 登录您的微信账号
3. 在微信设置中允许通知权限

## 项目构建

### 1. 使用Android Studio打开项目

1. 启动Android Studio
2. 选择**Open an Existing Project**
3. 浏览并选择克隆的项目目录

### 2. 解决依赖问题

等待Gradle同步完成。如果出现依赖错误，可能需要：

1. 更新Gradle插件和Gradle版本
2. 确保已启用Maven Central仓库
3. 检查项目的`build.gradle`和`settings.gradle`

### 3. 检查项目配置

1. 确保项目已配置正确的SDK版本（与Redmi 9A兼容）:
   - 打开`app/build.gradle`文件
   - 检查`compileSdkVersion`和`targetSdkVersion`是否为34（或更高）
   - 确保`minSdkVersion`不高于24

2. **代码审查后的更新**：我们已改进了微信元素ID适配逻辑，项目现在可以处理多种可能的ID模式：
   ```kotlin
   // RPAAccessibilityService.kt中的微信UI元素ID适配
   val possibleIds = listOf(
       "com.tencent.mm:id/b5q",  // 主要目标ID
       "com.tencent.mm:id/ij",   // 备选ID
       "com.tencent.mm:id/kl"    // 备选ID
   )
   
   // 自动尝试多种ID直到找到有效的
   for (id in possibleIds) {
       messageNodes = rootNode.findAccessibilityNodeInfosByViewId(id)
       if (messageNodes.isNotEmpty()) break
   }
   ```

## 部署到设备

### 1. 构建调试版本

1. 确保Redmi 9A已通过USB连接且已授权调试
2. 在Android Studio中点击工具栏上的**运行**按钮（绿色三角形）
3. 在弹出的设备选择对话框中选择Redmi 9A
4. 等待应用构建并安装

### 2. 查看调试日志

在Android Studio中打开**Logcat**视图，使用过滤器查看应用日志：

```
// 用于主应用日志
tag:SQRPAApplication

// 用于辅助功能服务日志
tag:RPAAccessibilityService

// 用于规则引擎日志
tag:RuleEngine

// 用于脚本引擎日志 
tag:ScriptEngine
```

我们最近增加了更详细的日志记录，可以更容易地跟踪问题。

## 辅助功能配置

### 1. 启用辅助功能服务

在应用首次启动后：

1. 点击应用主界面上的**启用辅助功能**按钮
2. 系统会跳转到**辅助功能设置**页面
3. 找到并点击**松鼠RPA**
4. 打开**使用松鼠RPA**开关
5. 在权限请求对话框中点击**允许**

### 2. 验证服务状态

1. 返回应用主界面
2. 检查服务状态是否显示为**已运行**
3. 如果状态未更新，可以尝试重启应用

## 微信元素适配

由于不同版本的微信，其UI元素的ID可能会有所不同，需要针对微信8.0.42版本进行适配。

### 1. 查找正确的元素ID

**项目更新**：现在项目会自动尝试多种可能的元素ID，但如果都失败，您可能需要手动查找正确的ID：

1. 使用Android Studio的**Layout Inspector**工具：
   - 连接Redmi 9A
   - 打开微信并进入一个聊天界面
   - 在Android Studio中选择**View > Tool Windows > Layout Inspector**
   - 选择进程**com.tencent.mm**
   - 捕获布局
   - 在布局树中找到消息文本、输入框和发送按钮元素
   - 记录它们的资源ID

2. 或者使用**UIAutomatorViewer**工具：
   - 位于`{SDK_PATH}/tools/bin/uiautomatorviewer`
   - 运行工具并捕获屏幕
   - 检查UI元素并记录资源ID

### 2. 更新代码中的元素ID列表

如果自动尝试的ID都不匹配，您可以将新发现的ID添加到列表中：

```kotlin
// 在RPAAccessibilityService.kt中找到这些列表并添加您发现的ID
val possibleIds = listOf(
    "com.tencent.mm:id/b5q",
    "com.tencent.mm:id/ij",
    "com.tencent.mm:id/kl",
    "com.tencent.mm:id/您找到的新ID"  // 添加这一行
)
```

### 3. 重新构建和测试

1. 修改完ID后，重新构建应用并部署到Redmi 9A
2. 打开微信，测试消息监听和自动回复功能

## 常见问题解决

### 1. 应用崩溃

**问题**: 应用在启动或使用过程中崩溃。
**解决方法**:
- 查看Logcat日志确定崩溃原因
- 检查权限是否正确授予
- 确保数据库初始化正确
- **更新**: 我们已增强错误处理，大多数常见崩溃应该已被处理

### 2. 无法检测微信消息

**问题**: 辅助功能服务无法检测到微信消息。
**解决方法**:
- 检查服务是否正确运行（主界面应显示"已运行"）
- 检查Logcat中有关ID匹配的日志（使用tag:RPAAccessibilityService过滤）
- 如果看到"通过ID未找到消息节点"的日志，请按照上述微信元素适配部分更新ID
- 尝试重启应用和微信

### 3. 小米系统特殊设置

**问题**: MIUI系统对后台应用有额外限制。
**解决方法**:
1. 打开手机**设置**
2. 进入**应用管理** > **松鼠RPA**
3. 选择**省电策略** > **无限制**
4. 开启**自启动**权限
5. 允许**显示悬浮窗**权限

### 4. 辅助功能服务被系统关闭

**问题**: 辅助功能服务被MIUI系统优化功能自动关闭。
**解决方法**:
- 进入**开发者选项** > **辅助功能检测**，确保不会被系统优化关闭
- 在**应用管理**中锁定应用，防止被系统清理

### 5. 脚本执行错误

**问题**: JavaScript脚本执行报错或不生成预期回复。
**解决方法**:
- 在Logcat中搜索"ScriptEngine"标签查看详细错误信息
- **更新**: 我们现在提供更详细的脚本错误信息，包括行号和上下文
- 检查脚本语法和API使用是否正确
- 使用简单脚本进行测试，如:
  ```javascript
  function processMessage(message, sender) {
      log.info("测试脚本");
      return "测试回复";
  }
  ```

## 高级调试技巧

### 1. 使用断点调试

Android Studio允许您设置断点并进行调试：

1. 在关键代码处设置断点（点击行号旁边）
2. 以调试模式运行应用
3. 当执行到断点时，您可以：
   - 检查变量值
   - 单步执行代码
   - 评估表达式

### 2. 动态修改日志级别

可以在应用运行时调整日志级别以获取更多信息：

```kotlin
// 在调试控制台中执行
adb shell setprop log.tag.RPAAccessibilityService VERBOSE
adb shell setprop log.tag.ScriptEngine VERBOSE
```

### 3. 调试数据库

如果遇到数据库相关问题：

1. 使用Android Studio的Database Inspector工具
2. 或导出数据库文件进行离线分析：
   ```bash
   adb shell run-as com.sq.rpa cp /data/data/com.sq.rpa/databases/sq_rpa_database /sdcard/
   adb pull /sdcard/sq_rpa_database
   ```

### 4. 分析性能问题

如果应用响应慢或占用资源高：

1. 使用Android Studio的Profiler工具
2. 关注CPU、内存和网络使用情况
3. 查找潜在瓶颈

## 结语

通过本指南，您应该能够在Redmi 9A上成功运行和调试松鼠RPA项目，并使其与微信8.0.42版本正常配合工作。我们已经增强了代码的错误处理和自适应能力，使项目更加健壮。

如果遇到其他问题，请查阅项目源码或联系开发团队获取更多支持。

祝您调试顺利！ 