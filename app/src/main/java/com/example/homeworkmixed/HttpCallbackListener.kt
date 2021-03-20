package com.example.homeworkmixed

interface HttpCallbackListener {
    fun onFinish(response: String)
    fun onError(e: Exception)
}