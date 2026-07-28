package kr.annyeong.app.data

import android.content.ContentValues
import android.content.Context
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 폰에 저장된 연락처를 읽고(READ_CONTACTS),
 * '가까운 친구'를 즐겨찾기(별표)로 표시(WRITE_CONTACTS)하는 저장소.
 */
class ContactsRepository(private val context: Context) {

    /** 이름·번호가 있는 모든 연락처를 이름순으로 불러온다. */
    suspend fun loadContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val result = LinkedHashMap<Long, Contact>()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.STARRED,
        )
        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} COLLATE LOCALIZED ASC",
        )?.use { c ->
            val idIdx = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIdx = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numIdx = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val starIdx = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.STARRED)
            while (c.moveToNext()) {
                val id = c.getLong(idIdx)
                val name = c.getString(nameIdx) ?: continue
                val number = c.getString(numIdx) ?: continue
                // 연락처당 첫 번째 번호만 대표로 사용
                if (!result.containsKey(id)) {
                    result[id] = Contact(
                        id = id,
                        name = name,
                        phone = number,
                        starred = c.getInt(starIdx) == 1,
                    )
                }
            }
        }
        result.values.toList()
    }

    /** 연락처를 즐겨찾기(별표)에 추가/해제 → '가까운 친구' 목록으로 활용. */
    suspend fun setStarred(contactId: Long, starred: Boolean) = withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put(ContactsContract.Contacts.STARRED, if (starred) 1 else 0)
        }
        context.contentResolver.update(
            ContactsContract.Contacts.CONTENT_URI,
            values,
            "${ContactsContract.Contacts._ID} = ?",
            arrayOf(contactId.toString()),
        )
    }
}
