package com.sq.rpa.script

import android.util.Log
import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.ScriptableObject
import java.util.concurrent.ConcurrentHashMap

/**
 * 脚本引擎
 * 基于Rhino JavaScript引擎，提供脚本执行环境
 */
class ScriptEngine private constructor() {
    companion object {
        private const val TAG = "ScriptEngine"
        
        @Volatile
        private var instance: ScriptEngine? = null
        
        fun getInstance(): ScriptEngine {
            return instance ?: synchronized(this) {
                instance ?: ScriptEngine().also { instance = it }
            }
        }
    }
    
    // 存储已加载的脚本
    private val scripts = ConcurrentHashMap<String, CompiledScript>()
    
    // JavaScript上下文
    private val rhino: RhinoEvaluator by lazy { RhinoEvaluator() }
    
    /**
     * 注册脚本
     * @param scriptId 脚本ID
     * @param scriptContent 脚本内容
     * @return 是否注册成功
     */
    fun registerScript(scriptId: String, scriptContent: String): Boolean {
        return try {
            val compiledScript = rhino.compile(scriptContent)
            scripts[scriptId] = compiledScript
            Log.d(TAG, "脚本注册成功: $scriptId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "脚本注册失败: $scriptId", e)
            false
        }
    }
    
    /**
     * 移除脚本
     * @param scriptId 脚本ID
     */
    fun removeScript(scriptId: String) {
        scripts.remove(scriptId)
        Log.d(TAG, "脚本已移除: $scriptId")
    }
    
    /**
     * 执行脚本
     * @param scriptId 脚本ID
     * @param params 参数映射
     * @return 执行结果
     */
    fun executeScript(scriptId: String, params: Map<String, Any>): Any? {
        val compiledScript = scripts[scriptId] ?: run {
            Log.e(TAG, "脚本未找到: $scriptId")
            return null
        }
        
        return try {
            rhino.execute(compiledScript, params)
        } catch (e: Exception) {
            Log.e(TAG, "脚本执行失败: $scriptId", e)
            null
        }
    }
    
    /**
     * 执行消息处理脚本
     * @param scriptId 脚本ID
     * @param message 消息内容
     * @param sender 发送者
     * @return 回复消息，null表示不回复
     */
    fun processChatMessage(scriptId: String, message: String, sender: String): String? {
        if (!scripts.containsKey(scriptId)) {
            Log.w(TAG, "脚本未找到: $scriptId")
            return null
        }
        
        Log.d(TAG, "开始执行脚本: $scriptId, 消息: $message, 发送者: $sender")
        
        return try {
            val startTime = System.currentTimeMillis()
            val result = executeScript(scriptId, mapOf(
                "message" to message,
                "sender" to sender,
                "timestamp" to System.currentTimeMillis()
            )) as? String
            val executionTime = System.currentTimeMillis() - startTime
            
            Log.d(TAG, "脚本执行完成: $scriptId, 耗时: ${executionTime}ms, 结果: $result")
            result
        } catch (e: Exception) {
            Log.e(TAG, "脚本执行异常: $scriptId", e)
            // 考虑返回一个错误回复，而不是默默失败
            null
        }
    }
    
    /**
     * 判断脚本是否存在
     * @param scriptId 脚本ID
     * @return 是否存在
     */
    fun hasScript(scriptId: String): Boolean {
        return scripts.containsKey(scriptId)
    }
    
    /**
     * 获取所有脚本ID
     * @return 脚本ID列表
     */
    fun getAllScriptIds(): List<String> {
        return scripts.keys.toList()
    }
}

/**
 * Rhino JavaScript评估器
 */
private class RhinoEvaluator {
    // 编译脚本
    fun compile(scriptContent: String): CompiledScript {
        val cx = Context.enter()
        try {
            cx.optimizationLevel = -1
            val scope = cx.initStandardObjects()
            
            // 注入API
            injectAPIs(cx, scope)
            
            try {
                // 编译脚本
                val jsScript = cx.compileString(scriptContent, "script", 1, null)
                return CompiledScript(jsScript, scope)
            } catch (e: Exception) {
                Log.e("ScriptEngine", "脚本编译错误: ${e.message}")
                // 提取更有用的错误信息
                val errorMessage = e.message ?: "未知编译错误"
                val lineNumber = if (errorMessage.contains("line")) {
                    errorMessage.substringAfter("line").substringBefore(":").trim().toIntOrNull() ?: 0
                } else 0
                
                // 如果有行号，提取问题行前后代码以提供上下文
                if (lineNumber > 0) {
                    val lines = scriptContent.lines()
                    val contextStart = maxOf(0, lineNumber - 3)
                    val contextEnd = minOf(lines.size, lineNumber + 2)
                    val context = lines.subList(contextStart, contextEnd)
                    
                    Log.e("ScriptEngine", "错误上下文 (行 $contextStart-$contextEnd):")
                    context.forEachIndexed { i, line ->
                        val lineNum = contextStart + i + 1
                        val prefix = if (lineNum == lineNumber) ">>> " else "    "
                        Log.e("ScriptEngine", "$prefix$lineNum: $line")
                    }
                }
                
                throw e
            }
        } finally {
            Context.exit()
        }
    }
    
    // 执行编译后的脚本
    fun execute(compiledScript: CompiledScript, params: Map<String, Any>): Any? {
        val cx = Context.enter()
        try {
            cx.optimizationLevel = -1
            val scope = compiledScript.scope
            
            // 注入参数
            for ((key, value) in params) {
                ScriptableObject.putProperty(scope, key, Context.javaToJS(value, scope))
            }
            
            // 执行脚本
            val result = compiledScript.script.exec(cx, scope)
            
            // 查找并调用处理函数
            val processFunc = scope.get("processMessage", scope) as? Function
            return if (processFunc != null) {
                val jsResult = processFunc.call(
                    cx, scope, scope, 
                    arrayOf(params["message"], params["sender"])
                )
                Context.jsToJava(jsResult, String::class.java)
            } else {
                null
            }
        } finally {
            Context.exit()
        }
    }
    
    // 注入API
    private fun injectAPIs(cx: Context, scope: ScriptableObject) {
        // TODO: 注入更多API
        
        // 注入日志API
        val logObj = cx.newObject(scope)
        ScriptableObject.putProperty(logObj, "debug", Context.javaToJS({ msg: String -> 
            Log.d("ScriptAPI", msg)
        }, scope))
        ScriptableObject.putProperty(logObj, "info", Context.javaToJS({ msg: String -> 
            Log.i("ScriptAPI", msg)
        }, scope))
        ScriptableObject.putProperty(logObj, "error", Context.javaToJS({ msg: String -> 
            Log.e("ScriptAPI", msg)
        }, scope))
        ScriptableObject.putProperty(scope, "log", logObj)
        
        // 注入HTTP API
        // TODO: 实现HTTP请求API
    }
}

/**
 * 编译后的脚本
 */
private data class CompiledScript(
    val script: org.mozilla.javascript.Script,
    val scope: ScriptableObject
) 