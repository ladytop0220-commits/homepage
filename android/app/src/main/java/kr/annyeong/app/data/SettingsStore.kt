package kr.annyeong.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 앱 전역 DataStore (기기 내부에만 저장)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "annyeong_settings")

/**
 * 사용자 설정 저장소.
 * - senderName : 안부 문자 서명에 쓸 내 이름
 * - closeThreshold : '가까운 친구' 판정 기준(한 달 연락 횟수)
 * - autoCheckIn : 매일 자동 안부 문자 발송 여부
 * - checkInHour : 자동 발송 시각(시)
 * 모든 값은 오직 이 기기에만 저장된다.
 */
class SettingsStore(private val context: Context) {

    private object Keys {
        val SENDER_NAME = stringPreferencesKey("sender_name")
        val CLOSE_THRESHOLD = intPreferencesKey("close_threshold")
        val AUTO_CHECKIN = booleanPreferencesKey("auto_checkin")
        val CHECKIN_HOUR = intPreferencesKey("checkin_hour")
        val CHECKIN_RECIPIENTS = stringPreferencesKey("checkin_recipients")
    }

    val senderName: Flow<String> = context.dataStore.data.map { it[Keys.SENDER_NAME] ?: "" }
    val closeThreshold: Flow<Int> = context.dataStore.data.map { it[Keys.CLOSE_THRESHOLD] ?: 1 }
    val autoCheckIn: Flow<Boolean> = context.dataStore.data.map { it[Keys.AUTO_CHECKIN] ?: false }
    val checkInHour: Flow<Int> = context.dataStore.data.map { it[Keys.CHECKIN_HOUR] ?: 10 }

    /** 자동 안부 문자를 받을 사람들의 전화번호 목록(줄바꿈 구분) */
    val recipients: Flow<List<String>> = context.dataStore.data.map { prefs ->
        (prefs[Keys.CHECKIN_RECIPIENTS] ?: "")
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    suspend fun setSenderName(value: String) =
        context.dataStore.edit { it[Keys.SENDER_NAME] = value }

    suspend fun setCloseThreshold(value: Int) =
        context.dataStore.edit { it[Keys.CLOSE_THRESHOLD] = value.coerceIn(1, 30) }

    suspend fun setAutoCheckIn(value: Boolean) =
        context.dataStore.edit { it[Keys.AUTO_CHECKIN] = value }

    suspend fun setCheckInHour(value: Int) =
        context.dataStore.edit { it[Keys.CHECKIN_HOUR] = value.coerceIn(0, 23) }

    suspend fun setRecipients(numbers: List<String>) =
        context.dataStore.edit { it[Keys.CHECKIN_RECIPIENTS] = numbers.joinToString("\n") }
}
