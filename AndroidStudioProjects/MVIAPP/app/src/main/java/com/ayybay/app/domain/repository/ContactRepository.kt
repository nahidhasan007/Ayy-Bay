package com.ayybay.app.domain.repository

import com.ayybay.app.domain.model.Contact

interface ContactRepository {
    suspend fun getContacts(): List<Contact>
}
