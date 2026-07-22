package kr.annyeong.app.data

import android.content.Context
import android.provider.CallLog
import android.provider.Telephony
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 최근 한 달간 통화기록(READ_CALL_LOG)과 문자기록(READ_SMS)을 분석해,
 * "자주 연락하는 친구"를 자동으로 선별한다.
 */
class CallLogRepository(private val context: Context) {

    /**
     * @param contacts 폰 연락처 목록
     * @param days 분석 기간(기본 30일 = 한 달)
     * @return 연락 빈도가 높은 순으로 정렬된 목록
     */
    suspend fun analyzeFrequency(
        contacts: List<Contact>,
        days: Int = 30,
    ): List<ContactFrequency> = withContext(Dispatchers.IO) {
        val since = System.currentTimeMillis() - days.toLong() * 24 * 60 * 60 * 1000

        val callCounts = HashMap<String, Int>()
        val smsCounts = HashMap<String, Int>()
        val lastContact = HashMap<String, Long>()

        // --- 통화기록 ---
        context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            arrayOf(CallLog.Calls.NUMBER, CallLog.Calls.DATE),
            "${CallLog.Calls.DATE} >= ?",
            arrayOf(since.toString()),
            null,
        )?.use { c ->
            val numIdx = c.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
            val dateIdx = c.getColumnIndexOrThrow(CallLog.Calls.DATE)
            while (c.moveToNext()) {
                val key = normalize(c.getString(numIdx)) ?: continue
                callCounts[key] = (callCounts[key] ?: 0) + 1
                val date = c.getLong(dateIdx)
                if (date > (lastContact[key] ?: 0)) lastContact[key] = date
            }
        }

        // --- 문자기록 (수신함 + 발신함) ---
        context.contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.DATE),
            "${Telephony.Sms.DATE} >= ?",
            arrayOf(since.toString()),
            null,
        )?.use { c ->
            val addrIdx = c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
            val dateIdx = c.getColumnIndexOrThrow(Telephony.Sms.DATE)
            while (c.moveToNext()) {
                val key = normalize(c.getString(addrIdx)) ?: continue
                smsCounts[key] = (smsCounts[key] ?: 0) + 1
                val date = c.getLong(dateIdx)
                if (date > (lastContact[key] ?: 0)) lastContact[key] = date
            }
        }

        contacts.mapNotNull { contact ->
            val key = normalize(contact.phone) ?: return@mapNotNull null
            val calls = callCounts[key] ?: 0
            val sms = smsCounts[key] ?: 0
            if (calls == 0 && sms == 0) return@mapNotNull null
            ContactFrequency(
                contact = contact,
                callCount = calls,
                smsCount = sms,
                lastContactMillis = lastContact[key] ?: 0L,
            )
        }.sortedByDescending { it.totalCount }
    }

    /**
     * 전화번호를 비교 가능한 형태로 정규화.
     * 국가번호·하이픈·공백을 제거하고 뒤 8자리로 매칭(국내 번호 기준).
     */
    private fun normalize(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val digits = raw.filter { it.isDigit() }
        if (digits.isEmpty()) return null
        return if (digits.length >= 8) digits.takeLast(8) else digits
    }
}
