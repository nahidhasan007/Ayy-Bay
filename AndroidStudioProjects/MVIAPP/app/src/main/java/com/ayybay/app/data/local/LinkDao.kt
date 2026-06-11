package com.ayybay.app.data.local

import androidx.room.*
import com.ayybay.app.data.local.entity.LinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkDao {

    @Query("SELECT * FROM links ORDER BY addedDate DESC")
    fun getAllLinks(): Flow<List<LinkEntity>>

    @Query("SELECT * FROM links WHERE category = :category ORDER BY addedDate DESC")
    fun getLinksByCategory(category: String): Flow<List<LinkEntity>>

    @Query("SELECT * FROM links WHERE id = :id")
    fun getLinkById(id: Long): Flow<LinkEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: LinkEntity): Long

    @Delete
    suspend fun deleteLink(link: LinkEntity)

    @Query("SELECT COUNT(*) FROM links WHERE category = :category")
    fun getLinkCountByCategory(category: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM links")
    suspend fun getTotalLinkCount(): Int
}
