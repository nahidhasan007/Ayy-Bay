package com.ayybay.app.presentation.mvi

import com.ayybay.app.domain.model.Contact

data class PhoneBookUiState(
    val allContacts: List<Contact> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val hasPermission: Boolean = false,
    val error: String? = null
) {
    val visibleContacts: List<Contact>
        get() = if (searchQuery.isBlank()) {
            allContacts
        } else {
            allContacts.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                    it.phoneNumber.contains(searchQuery, ignoreCase = true)
            }
        }
}
