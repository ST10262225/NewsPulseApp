package com.example.newspulse.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: NewsDataApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.mediastack.com/v1/") //url for news updates
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsDataApi::class.java)
    }
}
