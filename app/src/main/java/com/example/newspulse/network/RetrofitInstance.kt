package com.example.newspulse.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: NewsDataApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.mediastack.com/v1/") // HTTP for free plan
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsDataApi::class.java)
    }
}
