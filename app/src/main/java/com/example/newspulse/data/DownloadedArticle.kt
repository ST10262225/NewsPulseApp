package com.example.newspulse.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_articles")
data class DownloadedArticle(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val title_fr: String? = null,
    val title_es: String? = null,
    val title_pt: String? = null,
    val title_af: String? = null,
    val description: String,
    val description_fr: String? = null,
    val description_es: String? = null,
    val description_pt: String? = null,
    val description_af: String? = null,
    val imageUrl: String? = null,
    val content: String? = null,
    var language: String = "en" // current displayed language
)
