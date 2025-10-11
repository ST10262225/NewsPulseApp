package com.example.newspulse.data

data class NewsDataResponse(
    val pagination: Pagination?,
    val data: List<NewsDataItem>?
)

data class NewsDataItem(
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val source: String?,
    val image: String?,
    val category: String?,
    val language: String?,
    val country: String?,
    val published_at: String?
)

data class Pagination(
    val limit: Int?,
    val offset: Int?,
    val count: Int?,
    val total: Int?
)


