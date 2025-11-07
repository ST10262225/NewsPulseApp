package com.example.newspulse

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newspulse.R
import com.example.newspulse.data.AppDatabase
import com.example.newspulse.data.DownloadedArticle
import com.example.newspulse.data.News
import com.example.newspulse.network.RetrofitInstance
import com.example.newspulse.ui.NewsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : BaseActivity() {

    private var isFetching = false
    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private val newsList = mutableListOf<News>()
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: ImageButton

    private val apiKey = "a1aa53bfb34d80cc0cd23dbe4eb17f94"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize RecyclerView & Search
        recyclerView = findViewById(R.id.recyclerHeadlines)
        recyclerView.layoutManager = LinearLayoutManager(this)
        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)

        newsAdapter = NewsAdapter(newsList) { article ->
            // Save article to downloaded articles with current app language
            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getInstance(this@HomeActivity)
                val prefs = getSharedPreferences("SettingsPref", MODE_PRIVATE)
                val currentLanguage = prefs.getString("appLanguage", "en") ?: "en"

                val downloadedArticle = DownloadedArticle(
                    title = article.title,
                    description = article.description,
                    imageUrl = article.imageUrl ?: "",
                    content = "",
                    language = currentLanguage
                )


                db.downloadedArticleDao().insert(downloadedArticle)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@HomeActivity,
                        getString(R.string.article_downloaded),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        recyclerView.adapter = newsAdapter

        // Search button
        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            fetchNews(if (query.isEmpty()) null else query)
        }

        // Bottom navigation buttons
        findViewById<ImageButton>(R.id.ivDownloads).setOnClickListener {
            startActivity(Intent(this, DownloadedArticlesActivity::class.java))
        }

        findViewById<ImageButton>(R.id.ivWeather).setOnClickListener {
            startActivity(Intent(this, WeatherActivity::class.java))
        }

        findViewById<ImageView>(R.id.ivSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<ImageView>(R.id.ivRefresh).setOnClickListener {
            fetchNews()
        }

        // Initial fetch
        fetchNews()
    }

    private fun fetchNews(searchQuery: String? = null) {
        if (isFetching) return
        isFetching = true

        val prefs = getSharedPreferences("SettingsPref", MODE_PRIVATE)
        val langCode = prefs.getString("appLanguage", "en") ?: "en"

        // Mediastack API only supports English
        val apiLanguage = when (langCode) {
            "en" -> "en"
            else -> "en" // fallback for all other languages
        }

        if (langCode != "en") {
            Toast.makeText(
                this,
                "News articles are shown in English (API limitation).",
                Toast.LENGTH_SHORT
            ).show()
        }

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getNews(
                        apiKey = apiKey,
                        countries = "za",
                        languages = apiLanguage
                    )
                }

                isFetching = false

                if (response.isSuccessful) {
                    val articles = response.body()?.data?.map {
                        News(
                            title = it.title ?: getString(R.string.no_title),
                            description = it.description ?: getString(R.string.no_description),
                            imageUrl = it.image
                        )
                    } ?: emptyList()

                    val filteredArticles = if (searchQuery.isNullOrEmpty()) {
                        articles
                    } else {
                        articles.filter {
                            it.title.contains(searchQuery, ignoreCase = true) ||
                                    it.description.contains(searchQuery, ignoreCase = true)
                        }
                    }

                    if (filteredArticles.isEmpty()) {
                        Toast.makeText(
                            this@HomeActivity,
                            "No articles match your search.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    newsAdapter.updateNews(filteredArticles)

                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        "Error fetching news: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                isFetching = false
                e.printStackTrace()
                Toast.makeText(
                    this@HomeActivity,
                    "Exception: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
