package com.example.homeworkmixed.ui.home


import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.homeworkmixed.R
import com.example.homeworkmixed.network.HttpCallbackListener
import com.example.homeworkmixed.network.HttpUtil
import com.example.homeworkmixed.util.Downloader
import com.example.homeworkmixed.util.Paser
import com.example.homeworkmixed.util.PermissionCheckUtils
import com.example.homeworkmixed.util.loader.ImageLoader
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    //权限数组
    var permissionArray = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendHttpRequest()
        ImageLoader.build(this).bindBitmap(
            "https://img-blog.csdnimg.cn/img_convert/8d6aab726ad2cf78a416e99a1ff23a57.gif",
            ImageHolder
        )
        setBtn()
        PermissionCheckUtils.checkActivityPermissions(this, permissionArray, 100, null)
    }

    private fun setBtn() {
        btn.setOnClickListener {
            Downloader().build(
                url.text.toString().trim(),
                count.text.toString().trim().toInt(),
                ll_pb,
                applicationContext
            ).startDownload()
        }
    }

    //因为没有使用MVVM所以没有用用协程来简化回调写法
    private fun sendHttpRequest() {
        HttpUtil.sendHttpRequest("https://www.wanandroid.com/article/list/0/json",
            object : HttpCallbackListener {
                override fun onFinish(response: String) {
                    val parseJSONWithGson = Paser.parseJSONWithGson(response)
                    runOnUiThread {
                        homeloadingView.visibility = View.GONE
                        textView.visibility = View.VISIBLE
                        tv_article.visibility = View.VISIBLE
                        tv_article.text = parseJSONWithGson.data.datas[0].link
                    }
                }

                override fun onError(e: Exception) {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var flag = true
        for (i in grantResults) {
            //判断是否赋予权限的标记
            flag = if (i == PackageManager.PERMISSION_GRANTED) {
                flag && true
            } else {
                flag && false
            }
        }
        //如果权限赋予成功，那么久开始下载
        if (flag) {
            Downloader().build(
                url.text.toString().trim(),
                3,
                ll_pb,
                applicationContext
            ).startDownload()
        }
    }
}