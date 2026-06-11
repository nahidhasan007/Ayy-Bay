package com.ayybay.app.domain.repository

import com.ayybay.app.domain.model.DailyLink
import kotlinx.coroutines.flow.Flow

interface LinkRepository {

    fun getAllLinks(): Flow<List<DailyLink>>

    fun getLinksByCategory(category: String): Flow<List<DailyLink>>

    fun getLinkById(id: Long): Flow<DailyLink?>

    suspend fun insertLink(link: DailyLink): Long

    suspend fun deleteLink(link: DailyLink)

    fun getLinkCountByCategory(category: String): Flow<Int>

    suspend fun seedIfEmpty()
}
