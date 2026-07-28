package kr.annyeong.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kr.annyeong.app.data.CallLogRepository
import kr.annyeong.app.data.CommsManager
import kr.annyeong.app.data.Contact
import kr.annyeong.app.data.ContactFrequency
import kr.annyeong.app.data.ContactsRepository
import kr.annyeong.app.data.SettingsStore
import kr.annyeong.app.work.CheckInWorker

/** 앱 전체 화면이 공유하는 상태·동작을 모아둔 ViewModel. */
class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val appContext = app.applicationContext
    private val contactsRepo = ContactsRepository(app)
    private val callLogRepo = CallLogRepository(app)
    private val settings = SettingsStore(app)
    val comms = CommsManager(app)

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val _closeFriends = MutableStateFlow<List<ContactFrequency>>(emptyList())
    val closeFriends: StateFlow<List<ContactFrequency>> = _closeFriends.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    val senderName: StateFlow<String> =
        settings.senderName.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val closeThreshold: StateFlow<Int> =
        settings.closeThreshold.stateIn(viewModelScope, SharingStarted.Eagerly, 1)
    val autoCheckIn: StateFlow<Boolean> =
        settings.autoCheckIn.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val checkInHour: StateFlow<Int> =
        settings.checkInHour.stateIn(viewModelScope, SharingStarted.Eagerly, 10)
    val recipients: StateFlow<List<String>> =
        settings.recipients.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /** 연락처를 불러오고, 이어서 자주 연락하는 친구를 분석한다. */
    fun refresh() {
        viewModelScope.launch {
            _loading.value = true
            val list = contactsRepo.loadContacts()
            _contacts.value = list
            val threshold = closeThreshold.value
            _closeFriends.value = callLogRepo.analyzeFrequency(list)
                .filter { it.isCloseFriend(threshold) }
            _loading.value = false
        }
    }

    /** '가까운 친구'로 뽑힌 사람들을 폰 연락처 즐겨찾기(별표)에 자동 등록. */
    fun starCloseFriends() {
        viewModelScope.launch {
            _closeFriends.value.forEach { cf ->
                contactsRepo.setStarred(cf.contact.id, true)
            }
            refresh()
        }
    }

    fun setSenderName(v: String) = viewModelScope.launch { settings.setSenderName(v) }
    fun setCloseThreshold(v: Int) = viewModelScope.launch { settings.setCloseThreshold(v) }
    fun setRecipients(v: List<String>) = viewModelScope.launch { settings.setRecipients(v) }
    fun setCheckInHour(v: Int) = viewModelScope.launch { settings.setCheckInHour(v) }

    fun setAutoCheckIn(enabled: Boolean) {
        viewModelScope.launch {
            settings.setAutoCheckIn(enabled)
            if (enabled) {
                CheckInWorker.schedule(appContext, checkInHour.value)
            } else {
                CheckInWorker.cancel(appContext)
            }
        }
    }
}
