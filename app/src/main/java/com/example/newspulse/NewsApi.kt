package com.example.newspulse

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newspulse.data.AppDatabase
import com.example.newspulse.data.DownloadedArticle
import com.example.newspulse.data.News
import com.example.newspulse.network.RetrofitInstance
import com.example.newspulse.LocaleHelper
import com.example.newspulse.ui.NewsAdapter
import com.example.newspulse.utils.Translator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsApi : AppCompatActivity() {

    private lateinit var rvNews: RecyclerView
    private lateinit var btnShare: Button
    private lateinit var btnSettings: Button
    private lateinit var newsAdapter: NewsAdapter
    private val newsList = mutableListOf<News>()
    private var isFetching = false

    private val apiKey = "a1aa53bfb34d80cc0cd23dbe4eb17f94"
    private var selectedLang: String = "en"

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("SettingsPref", Context.MODE_PRIVATE)
        val lang = prefs.getString("appLanguage", "en") ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_news_api)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvNews = findViewById(R.id.rvNews)
        btnShare = findViewById(R.id.btnShare)
        btnSettings = findViewById(R.id.btnSettings)

        selectedLang = getSharedPreferences("SettingsPref", MODE_PRIVATE)
            .getString("appLanguage", "en") ?: "en"

        // ✅ Setup RecyclerView
        newsAdapter = NewsAdapter(newsList) { article ->
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // Use applicationContext to avoid crashes
                    val db = AppDatabase.getInstance(applicationContext)

                    val safeArticle = DownloadedArticle(
                        title = article.title ?: "Untitled",
                        description = article.description ?: "No description available",
                        imageUrl = article.imageUrl ?: "",
                        content = "",
                        language = selectedLang
                    )

                    db.downloadedArticleDao().insert(safeArticle)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@NewsApi,
                            "✅ Downloaded: ${safeArticle.title} (${selectedLang.uppercase()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@NewsApi,
                            "⚠️ Download failed: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        rvNews.layoutManager = LinearLayoutManager(this)
        rvNews.adapter = newsAdapter

        // Share button
        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this news on NewsPulse!")
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

        // Settings button
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        fetchNews()
    }

    private fun fetchNews() {
        if (isFetching) return
        isFetching = true

        lifecycleScope.launch {
            try {
                kotlinx.coroutines.delay(1500L)

                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getNews(
                        apiKey = apiKey,
                        countries = "za",
                        languages = "en",
                        categories = null
                    )
                }

                if (response.isSuccessful && response.body()?.data != null) {
                    val items = response.body()?.data?.map { article ->
                        val translatedTitle = withContext(Dispatchers.IO) {
                            Translator.translateText(article.title ?: "", selectedLang)
                        }
                        val translatedDesc = withContext(Dispatchers.IO) {
                            Translator.translateText(article.description ?: "", selectedLang)
                        }

                        News(
                            title = translatedTitle,
                            description = translatedDesc,
                            imageUrl = article.image ?: ""
                        )
                    } ?: emptyList()

                    newsAdapter.updateNews(items)

                    if (items.isEmpty()) {
                        Toast.makeText(this@NewsApi, "No articles found", Toast.LENGTH_SHORT).show()
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(this@NewsApi, "Unauthorized — check API key", Toast.LENGTH_LONG).show()
                } else if (response.code() == 429) {
                    Toast.makeText(this@NewsApi, "Too many requests, wait a bit", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        this@NewsApi,
                        "Error: ${response.code()} ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@NewsApi, "Exception: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                isFetching = false
            }
        }
    }
}
