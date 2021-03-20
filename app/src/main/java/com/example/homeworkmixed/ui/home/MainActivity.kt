package com.example.homeworkmixed.ui.home


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.homeworkmixed.R
import com.example.homeworkmixed.network.HttpCallbackListener
import com.example.homeworkmixed.network.HttpUtil
import com.example.homeworkmixed.util.Paser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        HttpUtil.sendHttpRequest("https://www.wanandroid.com/article/list/0/json",
            object : HttpCallbackListener {
                override fun onFinish(response: String) {
                    val parseJSONWithGson = Paser.parseJSONWithGson(response)
                    runOnUiThread {
                        homeloadingView.visibility= View.GONE
                        textView.visibility=View.VISIBLE
                        tv_article.visibility=View.VISIBLE
                        tv_article.text=parseJSONWithGson.data.datas[0].link
                    }
                }

                override fun onError(e: Exception) {
                    Toast.makeText(applicationContext,"Error",Toast.LENGTH_SHORT).show()

                }
            })
    }
}