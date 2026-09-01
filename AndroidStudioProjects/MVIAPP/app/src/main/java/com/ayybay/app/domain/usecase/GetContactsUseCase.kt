package com.ayybay.app.domain.usecase

import com.ayybay.app.domain.model.Contact
import com.ayybay.app.domain.repository.ContactRepository

class GetContactsUseCase(
    private val contactRepository: ContactRepository
) {
    suspend operator fun invoke(): List<Contact> = contactRepository.getContacts()
}
