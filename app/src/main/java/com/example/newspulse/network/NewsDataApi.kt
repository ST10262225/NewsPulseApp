package com.example.newspulse.network

import com.example.newspulse.data.NewsDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// interface for the api
interface NewsDataApi {
    @GET("news")
    suspend fun getNews(
        @Query("access_key") apiKey: String,
        @Query("countries") countries: String? = "za",
        @Query("languages") languages: String? = "en",
        @Query("categories") categories: String? = null
    ): Response<NewsDataResponse>
}
