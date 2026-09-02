package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.GovtJob
import com.ayybay.app.domain.model.JobTag

data class JobUiState(
    val allJobs: List<GovtJob> = emptyList(),
    val bookmarkedIds: Set<Long> = emptySet(),
    val selectedTag: JobTag? = null,
    val searchQuery: String = "",
    val bookmarkedOnly: Boolean = false
) {
    val featured: GovtJob?
        get() = allJobs.firstOrNull { it.isFeatured }

    val visibleJobs: List<GovtJob>
        get() = allJobs.filter { job ->
            (selectedTag == null || job.tags.contains(selectedTag)) &&
                (!bookmarkedOnly || bookmarkedIds.contains(job.id)) &&
                (searchQuery.isBlank() || job.title.contains(searchQuery, ignoreCase = true) || job.organization.contains(searchQuery, ignoreCase = true))
        }
}
