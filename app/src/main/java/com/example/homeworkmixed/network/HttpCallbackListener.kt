package com.example.homeworkmixed.network

interface HttpCallbackListener {
    fun onFinish(response: String)
    fun onError(e: Exception)
}