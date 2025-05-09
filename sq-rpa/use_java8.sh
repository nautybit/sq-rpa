#!/bin/bash
# 这个脚本将项目配置切换回Java 8和Gradle 7.4.2
# 修改gradle-wrapper.properties
echo "1. 更新gradle-wrapper.properties..."
echo "distributionBase=GRADLE_USER_HOME" > gradle/wrapper/gradle-wrapper.properties
echo "distributionPath=wrapper/dists" >> gradle/wrapper/gradle-wrapper.properties
echo "distributionUrl=https\\://mirrors.aliyun.com/gradle/gradle-7.4.2-bin.zip" >> gradle/wrapper/gradle-wrapper.properties
echo "zipStoreBase=GRADLE_USER_HOME" >> gradle/wrapper/gradle-wrapper.properties
echo "zipStorePath=wrapper/dists" >> gradle/wrapper/gradle-wrapper.properties
# 恢复Java 8配置
echo "2. 恢复Java 8配置..."
sed -i "" "s/# org.gradle.java.home=/org.gradle.java.home=/g" gradle.properties
echo "完成！现在您可以使用Java 8和Gradle 7.4.2了"
