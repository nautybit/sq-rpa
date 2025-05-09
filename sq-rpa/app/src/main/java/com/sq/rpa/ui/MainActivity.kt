package com.sq.rpa.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sq.rpa.R
import com.sq.rpa.accessibility.RPAAccessibilityService
import com.sq.rpa.ui.theme.SQRPATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SQRPATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    var isServiceRunning by remember { mutableStateOf(RPAAccessibilityService.isRunning) }
    var showDialog by remember { mutableStateOf(false) }
    
    // 检查辅助功能服务运行状态
    LaunchedEffect(Unit) {
        isServiceRunning = RPAAccessibilityService.isRunning
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("松鼠RPA") },
                actions = {
                    IconButton(onClick = { 
                        context.startActivity(Intent(context, SettingsActivity::class.java))
                    }) {
                        Icon(Icons.Filled.Settings, contentDescription = "设置")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 状态卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "服务状态",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 服务状态指示器
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    color = if (isServiceRunning) Color(0xFF4CAF50) else Color(0xFFE57373),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = if (isServiceRunning) "已运行" else "未运行",
                            fontSize = 16.sp,
                            color = if (isServiceRunning) Color(0xFF4CAF50) else Color(0xFFE57373)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            if (!isServiceRunning) {
                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                context.startActivity(intent)
                                showDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = if (isServiceRunning) "服务已启用" else "启用辅助功能")
                    }
                }
            }
            
            // 功能按钮区域
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "功能",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            // 跳转到规则管理界面
                            if (isServiceRunning) {
                                Toast.makeText(context, "规则管理功能正在开发中", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "请先启用辅助功能服务", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = isServiceRunning,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "规则管理")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = {
                            // 跳转到日志界面
                            Toast.makeText(context, "日志功能正在开发中", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "运行日志")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = {
                            // 跳转到脚本管理界面
                            context.startActivity(Intent(context, ScriptActivity::class.java))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "脚本管理")
                    }
                }
            }
            
            // 说明卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "使用说明",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "1. 点击「启用辅助功能」按钮\n" +
                               "2. 在系统设置中找到并启用「松鼠RPA」\n" +
                               "3. 在「脚本管理」中添加自动回复脚本\n" +
                               "4. 打开微信，程序将自动监听并回复消息",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
    
    // 辅助功能提示对话框
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("启用辅助功能") },
            text = { Text("请在系统设置中找到并点击「松鼠RPA」，然后打开开关以启用辅助功能服务。") },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("知道了")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SQRPATheme {
        MainScreen()
    }
} 