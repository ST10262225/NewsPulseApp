package com.example.newspulse

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newspulse.R
import com.example.newspulse.data.AppDatabase
import com.example.newspulse.data.DownloadedArticle
import com.example.newspulse.ui.DownloadedArticlesAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DownloadedArticlesActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DownloadedArticlesAdapter
    private lateinit var db: AppDatabase
    private lateinit var btnReturnHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_downloaded_articles)

        btnReturnHome = findViewById(R.id.btnReturnHome)
        recyclerView = findViewById(R.id.recyclerViewDownloaded)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = AppDatabase.getInstance(this)

        btnReturnHome.setOnClickListener { finish() }

        loadDownloadedArticles()
    }

    private fun loadDownloadedArticles() {
        lifecycleScope.launch(Dispatchers.IO) {
            val downloadedArticles = db.downloadedArticleDao().getAll()

            withContext(Dispatchers.Main) {
                if (downloadedArticles.isEmpty()) {
                    Toast.makeText(this@DownloadedArticlesActivity, getString(R.string.no_downloads), Toast.LENGTH_SHORT).show()
                }

                adapter = DownloadedArticlesAdapter(
                    downloadedArticles.toMutableList(),
                    onDeleteClick = { article ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            db.downloadedArticleDao().delete(article)
                            withContext(Dispatchers.Main) {
                                adapter.removeArticle(article)
                                Toast.makeText(this@DownloadedArticlesActivity, getString(R.string.article_deleted), Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onLongPress = { article ->
                        showLanguagePicker(article)
                    }
                )

                recyclerView.adapter = adapter
            }
        }
    }

    private fun showLanguagePicker(article: DownloadedArticle) {
        val languages = arrayOf("English", "French", "Spanish", "Portuguese", "Afrikaans")
        val codes = arrayOf("en", "fr", "es", "pt", "af")

        AlertDialog.Builder(this)
            .setTitle("Select Language")
            .setItems(languages) { _, which ->
                val newLang = codes[which]
                lifecycleScope.launch(Dispatchers.IO) {
                    val updatedArticle = article.copy(language = newLang)
                    db.downloadedArticleDao().update(updatedArticle)
                    withContext(Dispatchers.Main) {
                        adapter.updateArticle(updatedArticle)
                        Toast.makeText(this@DownloadedArticlesActivity, "Language updated to ${languages[which]}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }
}

