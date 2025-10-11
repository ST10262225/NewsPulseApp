package com.example.newspulse

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newspulse.data.News
import com.example.newspulse.network.RetrofitInstance
import com.example.newspulse.ui.NewsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private val newsList = mutableListOf<News>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerHeadlines)
        recyclerView.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter(newsList)
        recyclerView.adapter = newsAdapter

        // Bottom icons
        val download = findViewById<ImageButton>(R.id.ivDownloads)
        val weather = findViewById<ImageButton>(R.id.ivWeather)
        val sport = findViewById<ImageButton>(R.id.ivSports)
        val settings = findViewById<ImageView>(R.id.ivSettings)
        val refresh = findViewById<ImageView>(R.id.ivRefresh)

        download.setOnClickListener { startActivity(Intent(this, DownloadedArticlesActivity::class.java)) }
        weather.setOnClickListener { startActivity(Intent(this, WeatherActivity::class.java)) }
        sport.setOnClickListener { startActivity(Intent(this, SportsActivity::class.java)) }
        settings.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
        refresh.setOnClickListener { fetchNews() } // Refresh news

        // Category buttons
        val btnAll = findViewById<Button>(R.id.btnAll)
        val btnSports = findViewById<Button>(R.id.btnSports)
        val btnPolitics = findViewById<Button>(R.id.btnPolitics)
        val btnTechnology = findViewById<Button>(R.id.btnTechnology)
        val btnLifestyle = findViewById<Button>(R.id.btnLifestyle)
        val btnHealth = findViewById<Button>(R.id.btnHealth)

        btnAll.setOnClickListener { fetchNews() }
        btnSports.setOnClickListener { fetchNews("sports") }
        btnPolitics.setOnClickListener { fetchNews("politics") }
        btnTechnology.setOnClickListener { fetchNews("technology") }
        btnLifestyle.setOnClickListener { fetchNews("lifestyle") }
        btnHealth.setOnClickListener { fetchNews("health") }

        // Initial fetch
        fetchNews()
    }

    private fun fetchNews(category: String? = null) {
        val apiKey = "2cb4da3db72f20e42da820971c14a9c1"
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getNews(apiKey = apiKey, categories = category)
                }

                if (response.isSuccessful) {
                    val items = response.body()?.data?.map {
                        News(
                            title = it.title ?: "No title",
                            description = it.description ?: "No description",
                            imageUrl = it.image
                        )
                    } ?: emptyList()

                    newsAdapter.updateNews(items) // update RecyclerView
                } else {
                    Toast.makeText(this@HomeActivity, "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@HomeActivity, "Exception: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
