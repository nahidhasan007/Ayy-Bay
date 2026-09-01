package com.ayybay.app.data.repository

import android.content.Context
import android.provider.ContactsContract
import com.ayybay.app.domain.model.Contact
import com.ayybay.app.domain.repository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactRepositoryImpl(
    private val context: Context
) : ContactRepository {

    override suspend fun getContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = LinkedHashMap<String, Contact>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        )

        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex) ?: continue
                val number = cursor.getString(numberIndex)?.trim() ?: continue
                if (number.isBlank()) continue
                val photoUri = if (photoIndex >= 0) cursor.getString(photoIndex) else null

                // A contact can surface the same normalized number more than once across
                // synced accounts; key on id+number so we keep one row per real number.
                val key = "$id:${number.filter { it.isDigit() || it == '+' }}"
                if (!contacts.containsKey(key)) {
                    contacts[key] = Contact(id = id, name = name, phoneNumber = number, photoUri = photoUri)
                }
            }
        }

        contacts.values.sortedBy { it.name.lowercase() }
    }
}
