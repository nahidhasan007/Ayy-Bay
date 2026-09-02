package com.ayybay.app.domain.model

data class GovtJob(
    val id: Long,
    val organization: String,
    val organizationBn: String,
    val title: String,
    val titleBn: String = title,
    val logoEmoji: String,
    val publishedDate: String,
    val deadline: String,
    val vacancies: Int,
    val websiteUrl: String,
    val tags: Set<JobTag> = emptySet(),
    val isNew: Boolean = false,
    val isFeatured: Boolean = false
)

enum class JobTag(val label: String) {
    NEW("New"),
    DEADLINE_SOON("Deadline Soon"),
    BCS("BCS"),
    BANK("Bank"),
    DEFENSE("Defense")
}
