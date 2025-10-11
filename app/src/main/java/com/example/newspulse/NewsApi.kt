package com.example.newspulse

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newspulse.data.News
import com.example.newspulse.network.RetrofitInstance
import com.example.newspulse.ui.NewsAdapter
import com.google.android.gms.common.api.internal.ApiKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsApi : AppCompatActivity() {

    private lateinit var rvNews: RecyclerView
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnShare: Button
    private lateinit var btnSettings: Button
    private lateinit var newsAdapter: NewsAdapter
    private val newsList = mutableListOf<News>()

    private val apiKey = "2cb4da3db72f20e42da820971c14a9c1"


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




        // Initialize views
        rvNews = findViewById(R.id.rvNews)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnShare = findViewById(R.id.btnShare)
        btnSettings = findViewById(R.id.btnSettings)

        // Setup categories spinner
        val categories = arrayOf("All", "Technology", "Sports", "Politics")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = adapter

        // RecyclerView setup
        newsAdapter = NewsAdapter(newsList)
        rvNews.layoutManager = LinearLayoutManager(this)
        rvNews.adapter = newsAdapter

        // Initial news fetch (South Africa)
        fetchNews()  // fetch all news (ZA default)

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val category = if (categories[position] == "All") null else categories[position].lowercase()
                fetchNews(category)  // only pass the category
            }
            override fun onNothingSelected(parent: AdapterView<*>) { }
        }


        // Share button click
        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this news on NewsPulse!")
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

        // Settings button click
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun fetchNews(category: String? = null) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getNews(
                        apiKey = apiKey,
                        categories = category
                    )
                }

                if (response.isSuccessful) {
                    val items = response.body()?.data?.map {
                        News(
                            title = it.title ?: "No title",
                            description = it.description ?: "No description",
                            imageUrl = it.image ?: null

                        )
                    } ?: emptyList()

                    newsAdapter.updateNews(items)
                } else {
                    Toast.makeText(this@NewsApi, "Error: ${response.code()} ${response.message()}", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@NewsApi, "Exception: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}