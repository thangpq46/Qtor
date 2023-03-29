package com.example.qtor.data.repository

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ClipDropApi {
    @GET
    suspend fun downloadImage(@Url url: String): Response<ResponseBody>
}
