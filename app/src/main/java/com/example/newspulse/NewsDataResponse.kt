data class NewsDataResponse(
    val pagination: Pagination?,
    val data: List<NewsItem>?
)

data class Pagination(
    val limit: Int?,
    val offset: Int?,
    val count: Int?,
    val total: Int?
)

data class NewsItem(
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
