package com.example.homeworkmixed.network


import android.os.Looper
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 * 用协程简化回调写法
 */
//suspend fun request(address: String):String{
//    return suspendCoroutine {
//        continuation ->
//        HttpUtil.sendHttpRequest(address,object :HttpCallbackListener{
//            override fun onError(e: Exception) {
//                continuation.resumeWithException(e)
//            }
//
//            override fun onFinish(response: String) {
//                continuation.resume(response)
//            }
//        })
//    }
//}
object HttpUtil {



    fun sendHttpRequest(address: String, listener:HttpCallbackListener) {
        thread {
            Looper.prepare()
            var connection: HttpURLConnection? = null
            try {
                val response = StringBuilder()
                val url = URL(address)
                connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                val input = connection.inputStream
                val reader = BufferedReader(InputStreamReader(input))
                reader.use {
                    reader.forEachLine {
                        response.append(it)
                    }
                }
                // 回调onFinish()方法
                listener.onFinish(response.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                // 回调onError()方法
                listener.onError(e)
            } finally {
                connection?.disconnect()
            }
        }
    }

}