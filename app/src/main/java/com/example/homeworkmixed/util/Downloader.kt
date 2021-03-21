package com.example.homeworkmixed.util

import android.content.Context
import android.os.AsyncTask
import android.os.Environment
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.example.homeworkmixed.R
import java.io.File
import java.io.RandomAccessFile
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


class Downloader {

    //下载地址
    lateinit var path: String

    //线程程数量
    var threadCount = 1

    //添加一个集合用来存进度条的引用
    var pbLists = ArrayList<ProgressBar>()

    lateinit var container: LinearLayout

    lateinit var context: Context

    lateinit var hash: HashMap<Long, ProgressBar>


    fun build(
        path: String,
        threadCount: Int,
        container: LinearLayout,
        context: Context
    ): Downloader {
        this.path = "https://dldir1.qq.com/weixin/Windows/WeChatSetup.exe"
        this.threadCount = threadCount
        this.container = container
        this.context = context
        return this
    }


    fun startDownload() {
        for (i in 0 until threadCount) {
            val pbView = View.inflate(context, R.layout.item, null) as ProgressBar
            pbView.progress = 0
            pbLists.add(pbView)
            container.addView(pbView)
        }

        thread {
            //创建与文件地址绑定的连接
            var url =
                URL("https://dldir1.qq.com/weixin/Windows/WeChatSetup.exe")
            //打开文件连接对象
            val openConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            //设置请求方式
            openConnection.requestMethod = "GET"
            //设置连接时间
            openConnection.connectTimeout = 5000
            //获取网络连接的响应码
            val responseCode = openConnection.responseCode
            //响应码等于200，说明创建连接成功
            if (responseCode == 200) {
                //获取文件长度
                val contentLength = openConnection.contentLength
                //先创建一个跟将要下载文件大小相同的文件
                val sdPath = Environment.getExternalStorageDirectory().absolutePath
                //使用rw模式可以使用缓冲区，往硬盘中写数据
                val filePath = "$sdPath/tomcat.exe"
                var file = File(filePath)
                if (file.exists()) {
                    file.delete()
                }
                var raf = RandomAccessFile(filePath, "rw")
                //设置创建文件的大小
                raf.setLength(contentLength.toLong())
                raf.close()

                var blockSize = contentLength / threadCount
                for (i in 0 until threadCount) {
                    //开始下载的文件坐标
                    var start = i * blockSize
                    //下载截止的文件坐标
                    var end = (i + 1) * blockSize - 1
                    //最后一个线程下载截止的文件坐标
                    if (i == threadCount - 1) {
                        end = contentLength - 1
                    }
                    //启动相应协程，实现多协程下载
                    downloadByThread(start, end, sdPath, i)
                }
            }
        }

    }

    private fun downloadByThread(start: Int, end: Int, sdPath: String?, id: Int) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            reallyDownload(start, end, sdPath, id)
        }
    }

    private fun reallyDownload(start: Int, end: Int, sdPath: String?, Id: Int) {
        //重新创建连接，进行指定位置下载
        //创建与文件地址绑定的连接
        var url = URL(path)
        //打开文件连接对象
        val openConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        //设置请求方式
        openConnection.requestMethod = "GET"
        //设置连接时间
        openConnection.connectTimeout = 5000
        //在请求头中封装客户端所需要的数据
        openConnection.setRequestProperty("range", "bytes=$start-$end")
        //获取网络连接的响应码
        val responseCode = openConnection.responseCode
        //此时返回206的响应码才算是成功的
        if (responseCode == 206) {
            var inputStream = openConnection.inputStream
            //创建文件
            var raf = RandomAccessFile("$sdPath/tomcat.avi", "rw")
            //跳到指定位置
            raf.seek(start.toLong())

            var len = -1
            //设定缓冲区
            var buf = ByteArray(1024)
            var flag = true
            var progress = 0
            while (flag) {
                //读取数据，返回下标
                len = inputStream.read(buf)
                //TODO 可以在此处记录下载文件的坐标，从而实现断点下载
                flag = len != -1
                //写数据
                if (flag) {
                    raf.write(buf, 0, len)
                    progress += len
                }
                val progressBar = container.getChildAt(Id) as ProgressBar
                progressBar?.max = end - start
                progressBar?.progress = progress

//                val pbView = hash[threadId]
//
//                pbView?.max = end - start
//                pbView?.progress = progress

            }
            raf.close()
        }
    }
}