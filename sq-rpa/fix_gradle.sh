#!/bin/bash

# 这个脚本会直接下载Gradle并放置到正确位置，完全绕过Gradle Wrapper机制
# 使用方法: ./fix_gradle.sh

echo "===== 开始修复Gradle问题 ====="

# 1. 确定Gradle版本和下载URL
GRADLE_VERSION="7.4.2"
DOWNLOAD_URL="https://mirrors.cloud.tencent.com/gradle/gradle-${GRADLE_VERSION}-bin.zip"
TEMP_DIR="/tmp/gradle_fix"

# 2. 创建临时目录并下载
mkdir -p $TEMP_DIR
cd $TEMP_DIR
echo "1. 从腾讯云镜像下载Gradle ${GRADLE_VERSION}..."
curl -L -o gradle.zip $DOWNLOAD_URL

# 3. 检查下载是否成功
if [ ! -f gradle.zip ]; then
    echo "下载失败！请检查网络连接"
    exit 1
fi

# 4. 解压Gradle
echo "2. 解压Gradle..."
unzip -q gradle.zip

# 5. 创建本地Gradle目录
LOCAL_GRADLE="$PWD/../local_gradle"
mkdir -p $LOCAL_GRADLE
cp -r gradle-$GRADLE_VERSION $LOCAL_GRADLE/

# 6. 创建一个简单的启动脚本
echo "3. 创建启动脚本..."
cd ..
cat > run_with_local_gradle.sh << EOL
#!/bin/bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_251.jdk/Contents/Home
export PATH=\$JAVA_HOME/bin:\$PATH
./local_gradle/gradle-$GRADLE_VERSION/bin/gradle "\$@"
EOL
chmod +x run_with_local_gradle.sh

# 7. 清理临时文件
echo "4. 清理临时文件..."
rm -rf $TEMP_DIR

echo "===== Gradle修复完成 ====="
echo "现在您可以使用 ./run_with_local_gradle.sh build 来构建项目"
echo "这将完全绕过Gradle Wrapper机制，直接使用本地Gradle" 