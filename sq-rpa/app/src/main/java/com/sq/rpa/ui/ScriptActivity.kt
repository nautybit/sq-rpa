package com.sq.rpa.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sq.rpa.data.AppDatabase
import com.sq.rpa.model.ScriptInfo
import com.sq.rpa.script.ScriptEngine
import com.sq.rpa.ui.theme.SQRPATheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * 脚本管理界面
 */
class ScriptActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 获取数据库和脚本引擎
        val database = AppDatabase.getInstance(applicationContext)
        val scriptEngine = ScriptEngine.getInstance()
        
        setContent {
            SQRPATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScriptManagerScreen(
                        database = database,
                        scriptEngine = scriptEngine,
                        onBackPressed = { finish() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptManagerScreen(
    database: AppDatabase,
    scriptEngine: ScriptEngine,
    onBackPressed: () -> Unit
) {
    val scope = rememberCoroutineScope()
    
    // 获取脚本列表
    val scriptsFlow: Flow<List<ScriptInfo>> = remember {
        database.scriptInfoDao().getAllScripts()
    }
    val scripts by scriptsFlow.collectAsState(initial = emptyList())
    
    // 对话框状态
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingScript by remember { mutableStateOf<ScriptInfo?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("脚本管理") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "添加脚本")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 脚本列表
            if (scripts.isEmpty()) {
                // 空状态
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "还没有脚本",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showAddDialog = true }) {
                        Text("添加脚本")
                    }
                }
            } else {
                // 脚本列表
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(scripts) { script ->
                        ScriptItem(
                            script = script,
                            onEdit = {
                                editingScript = script
                                showEditDialog = true
                            },
                            onDelete = {
                                scope.launch {
                                    // 从数据库删除
                                    database.scriptInfoDao().deleteById(script.id)
                                    // 从脚本引擎移除
                                    scriptEngine.removeScript(script.id)
                                }
                            },
                            onToggleEnabled = { enabled ->
                                scope.launch {
                                    database.scriptInfoDao().updateScriptEnabled(script.id, enabled)
                                    if (enabled) {
                                        // 重新加载脚本
                                        val updatedScript = database.scriptInfoDao().getById(script.id)
                                        if (updatedScript != null) {
                                            scriptEngine.registerScript(updatedScript.id, updatedScript.content)
                                        }
                                    } else {
                                        // 从引擎移除
                                        scriptEngine.removeScript(script.id)
                                    }
                                }
                            }
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
            
            // 添加脚本对话框
            if (showAddDialog) {
                ScriptEditDialog(
                    script = null,
                    onDismiss = { showAddDialog = false },
                    onSave = { id, name, content, description ->
                        scope.launch {
                            // 创建新脚本
                            val newScript = ScriptInfo(
                                id = id,
                                name = name,
                                content = content,
                                description = description
                            )
                            
                            // 保存到数据库
                            database.scriptInfoDao().insert(newScript)
                            
                            // 注册到脚本引擎
                            val success = scriptEngine.registerScript(id, content)
                            if (success) {
                                Toast.makeText(scope.coroutineContext.toString(), "脚本已添加", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(scope.coroutineContext.toString(), "脚本编译错误", Toast.LENGTH_SHORT).show()
                            }
                            
                            showAddDialog = false
                        }
                    }
                )
            }
            
            // 编辑脚本对话框
            if (showEditDialog && editingScript != null) {
                ScriptEditDialog(
                    script = editingScript,
                    onDismiss = { 
                        showEditDialog = false
                        editingScript = null
                    },
                    onSave = { id, name, content, description ->
                        scope.launch {
                            // 更新脚本
                            val updatedScript = editingScript!!.copy(
                                name = name,
                                content = content,
                                description = description,
                                updateTime = System.currentTimeMillis()
                            )
                            
                            // 保存到数据库
                            database.scriptInfoDao().update(updatedScript)
                            
                            // 更新脚本引擎
                            val success = scriptEngine.registerScript(id, content)
                            if (success) {
                                Toast.makeText(scope.coroutineContext.toString(), "脚本已更新", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(scope.coroutineContext.toString(), "脚本编译错误", Toast.LENGTH_SHORT).show()
                            }
                            
                            showEditDialog = false
                            editingScript = null
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ScriptItem(
    script: ScriptInfo,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleEnabled: (Boolean) -> Unit
) {
    var enabled by remember { mutableStateOf(script.enabled) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题栏
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = script.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                Switch(
                    checked = enabled,
                    onCheckedChange = { newValue ->
                        enabled = newValue
                        onToggleEnabled(newValue)
                    }
                )
            }
            
            // 描述
            if (script.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = script.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 操作按钮
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "编辑")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("编辑")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "删除")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("删除")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptEditDialog(
    script: ScriptInfo?,
    onDismiss: () -> Unit,
    onSave: (id: String, name: String, content: String, description: String) -> Unit
) {
    val isNewScript = script == null
    
    var scriptId by remember { mutableStateOf(script?.id ?: "") }
    var scriptName by remember { mutableStateOf(script?.name ?: "") }
    var scriptContent by remember { mutableStateOf(script?.content ?: "function processMessage(message, sender) {\n    // 在这里编写脚本逻辑\n    return \"回复: \" + message;\n}") }
    var scriptDescription by remember { mutableStateOf(script?.description ?: "") }
    
    var idError by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var contentError by remember { mutableStateOf(false) }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (isNewScript) "添加脚本" else "编辑脚本",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 脚本ID输入框
                TextField(
                    value = scriptId,
                    onValueChange = {
                        scriptId = it
                        idError = it.isBlank()
                    },
                    label = { Text("脚本ID") },
                    isError = idError,
                    enabled = isNewScript, // 只有新脚本可以修改ID
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (idError) {
                    Text(
                        text = "脚本ID不能为空",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 脚本名称输入框
                TextField(
                    value = scriptName,
                    onValueChange = {
                        scriptName = it
                        nameError = it.isBlank()
                    },
                    label = { Text("脚本名称") },
                    isError = nameError,
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (nameError) {
                    Text(
                        text = "脚本名称不能为空",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 脚本内容输入框
                TextField(
                    value = scriptContent,
                    onValueChange = {
                        scriptContent = it
                        contentError = it.isBlank()
                    },
                    label = { Text("脚本内容") },
                    isError = contentError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10
                )
                
                if (contentError) {
                    Text(
                        text = "脚本内容不能为空",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 脚本描述输入框
                TextField(
                    value = scriptDescription,
                    onValueChange = { scriptDescription = it },
                    label = { Text("脚本描述(可选)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 操作按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            idError = scriptId.isBlank()
                            nameError = scriptName.isBlank()
                            contentError = scriptContent.isBlank()
                            
                            if (!idError && !nameError && !contentError) {
                                onSave(scriptId, scriptName, scriptContent, scriptDescription)
                            }
                        }
                    ) {
                        Text("保存")
                    }
                }
            }
        }
    }
} 