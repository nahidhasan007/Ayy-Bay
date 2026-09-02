package com.ayybay.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ayybay.app.data.local.entity.JobBookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobBookmarkDao {

    @Query("SELECT jobId FROM job_bookmarks")
    fun getBookmarkedJobIds(): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun bookmark(entity: JobBookmarkEntity)

    @Query("DELETE FROM job_bookmarks WHERE jobId = :jobId")
    suspend fun unbookmark(jobId: Long)
}
