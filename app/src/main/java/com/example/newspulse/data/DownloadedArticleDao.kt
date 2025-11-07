package com.example.newspulse.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DownloadedArticleDao {

    @Insert
    suspend fun insert(article: DownloadedArticle)

    @Query("SELECT * FROM downloaded_articles")
    suspend fun getAll(): List<DownloadedArticle>

    @Delete
    suspend fun delete(article: DownloadedArticle)

    @Update
    suspend fun update(article: DownloadedArticle)
}
