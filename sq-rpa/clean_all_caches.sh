#!/bin/bash

echo "===== 开始彻底清理Gradle和Android Studio缓存 ====="

# 停止Gradle守护进程
echo "1. 停止Gradle守护进程..."
./gradlew --stop

# 删除项目特定缓存
echo "2. 删除项目缓存..."
rm -rf .gradle
rm -rf build
rm -rf app/build
rm -rf */build
rm -rf .idea/caches
rm -rf .idea/libraries
rm -rf .idea/modules.xml
rm -rf .idea/workspace.xml

# 删除全局Gradle缓存
echo "3. 删除全局Gradle缓存..."
rm -rf ~/.gradle/caches/
rm -rf ~/.gradle/daemon/
rm -rf ~/.gradle/wrapper/

# 删除Android缓存
echo "4. 删除Android缓存..."
rm -rf ~/.android/build-cache/
rm -rf ~/.android/cache/

# 删除IDE缓存
echo "5. 尝试删除IDE缓存..."
if [ -d ~/Library/Caches/Google/AndroidStudio* ]; then
    rm -rf ~/Library/Caches/Google/AndroidStudio*
    echo "   Android Studio缓存已删除"
else
    echo "   Android Studio缓存目录不存在或无法访问"
fi

if [ -d ~/Library/Application\ Support/Google/AndroidStudio* ]; then
    rm -rf ~/Library/Application\ Support/Google/AndroidStudio*/caches
    echo "   Android Studio Support缓存已删除"
else
    echo "   Android Studio Support缓存目录不存在或无法访问"
fi

# 显示Mac特定目录
echo "6. Mac特定缓存目录信息："
echo "   如果以下目录存在，可能也需要手动清理："
echo "   - ~/Library/Caches/Google/AndroidStudio*"
echo "   - ~/Library/Application Support/Google/AndroidStudio*/caches"

echo "===== 清理完成 ====="
echo "建议重启Android Studio并使用 File > Invalidate Caches / Restart... 选项"
echo "注意：首次重建项目时可能需要下载依赖，请耐心等待" 