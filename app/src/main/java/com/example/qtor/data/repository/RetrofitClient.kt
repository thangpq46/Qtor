package com.example.qtor.data.repository

import com.example.qtor.constant.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object RetrofitClient {
    private val client =
        OkHttpClient.Builder()
            .build()
    val api: ClipDropApi by lazy {
        retrofit.create(ClipDropApi::class.java)
    }
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .build()
    }
}