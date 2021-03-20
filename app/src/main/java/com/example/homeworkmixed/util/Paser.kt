package com.example.homeworkmixed.util


import com.example.homeworkmixed.bean.ArticleBean
import com.google.gson.Gson

object Paser {
    fun parseJSONWithGson(jsonData:String): ArticleBean {
        val gson = Gson()
        val articleList = gson.fromJson(jsonData,ArticleBean::class.java)
        return articleList
    }
}