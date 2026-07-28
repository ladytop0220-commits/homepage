package kr.annyeong.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kr.annyeong.app.data.Contact
import kr.annyeong.app.ui.AppViewModel
import kr.annyeong.app.ui.screens.CallScreen
import kr.annyeong.app.ui.screens.CloseFriendsScreen
import kr.annyeong.app.ui.screens.HomeScreen
import kr.annyeong.app.ui.screens.MessageScreen
import kr.annyeong.app.ui.screens.SettingsScreen
import kr.annyeong.app.ui.theme.AnnyeongTheme

enum class Screen(val title: String) {
    HOME("안녕"),
    CALL("전화 걸기"),
    MESSAGE("안부 보내기"),
    FRIENDS("가까운 친구"),
    SETTINGS("설정"),
}

class MainActivity : ComponentActivity() {

    private val vm: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnnyeongTheme {
                AppRoot(vm)
            }
        }
    }
}

private val REQUIRED_PERMISSIONS: Array<String> = buildList {
    add(Manifest.permission.READ_CONTACTS)
    add(Manifest.permission.WRITE_CONTACTS)
    add(Manifest.permission.CALL_PHONE)
    add(Manifest.permission.READ_CALL_LOG)
    add(Manifest.permission.READ_SMS)
    add(Manifest.permission.SEND_SMS)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        add(Manifest.permission.POST_NOTIFICATIONS)
    }
}.toTypedArray()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppRoot(vm: AppViewModel) {
    val context = LocalContext.current
    var screen by remember { mutableStateOf(Screen.HOME) }

    fun has(permission: String) =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        if (result[Manifest.permission.READ_CONTACTS] == true || has(Manifest.permission.READ_CONTACTS)) {
            vm.refresh()
        }
    }

    // 첫 실행 시 필요한 권한을 한 번에 요청, 이미 있으면 바로 데이터 로드
    LaunchedEffect(Unit) {
        val missing = REQUIRED_PERMISSIONS.any { !has(it) }
        if (missing) {
            permissionLauncher.launch(REQUIRED_PERMISSIONS)
        } else {
            vm.refresh()
        }
    }

    val contacts by vm.contacts.collectAsState()
    val closeFriends by vm.closeFriends.collectAsState()
    val loading by vm.loading.collectAsState()
    val senderName by vm.senderName.collectAsState()
    val threshold by vm.closeThreshold.collectAsState()
    val autoCheckIn by vm.autoCheckIn.collectAsState()
    val checkInHour by vm.checkInHour.collectAsState()
    val recipients by vm.recipients.collectAsState()

    val onCall: (Contact) -> Unit = { c ->
        vm.comms.call(c.phone, has(Manifest.permission.CALL_PHONE))
    }

    val onSend: (List<Contact>, String) -> Unit = { people, message ->
        if (people.isEmpty() || message.isBlank()) {
            Toast.makeText(context, "받는 사람과 내용을 확인해주세요.", Toast.LENGTH_SHORT).show()
        } else if (has(Manifest.permission.SEND_SMS)) {
            var ok = 0
            people.forEach { if (vm.comms.sendSmsDirect(it.phone, message)) ok++ }
            Toast.makeText(context, "${ok}명에게 안부 문자를 보냈어요.", Toast.LENGTH_LONG).show()
            screen = Screen.HOME
        } else {
            // 문자 권한이 없으면 문자앱을 열어 직접 보내도록 함(첫 번째 대상)
            vm.comms.openSmsApp(people.first().phone, message)
        }
    }

    if (screen != Screen.HOME) {
        BackHandler { screen = Screen.HOME }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screen.title, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    if (screen != Screen.HOME) {
                        IconButton(onClick = { screen = Screen.HOME }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { padding ->
        val content = Modifier.padding(padding)
        when (screen) {
            Screen.HOME -> androidx.compose.foundation.layout.Box(content) {
                HomeScreen(
                    contacts = contacts,
                    senderName = senderName,
                    hasCallPermission = has(Manifest.permission.CALL_PHONE),
                    onCall = onCall,
                    onGoCall = { screen = Screen.CALL },
                    onGoMessage = { screen = Screen.MESSAGE },
                    onGoFriends = { screen = Screen.FRIENDS },
                    onGoSettings = { screen = Screen.SETTINGS },
                )
            }

            Screen.CALL -> androidx.compose.foundation.layout.Box(content) {
                CallScreen(contacts = contacts, onCall = onCall)
            }

            Screen.MESSAGE -> androidx.compose.foundation.layout.Box(content) {
                MessageScreen(contacts = contacts, senderName = senderName, onSend = onSend)
            }

            Screen.FRIENDS -> androidx.compose.foundation.layout.Box(content) {
                CloseFriendsScreen(
                    closeFriends = closeFriends,
                    threshold = threshold,
                    loading = loading,
                    onCall = onCall,
                    onRegisterAll = { vm.starCloseFriends() },
                )
            }

            Screen.SETTINGS -> androidx.compose.foundation.layout.Box(content) {
                SettingsScreen(
                    contacts = contacts,
                    senderName = senderName,
                    threshold = threshold,
                    autoCheckIn = autoCheckIn,
                    checkInHour = checkInHour,
                    recipients = recipients,
                    onNameChange = vm::setSenderName,
                    onThresholdChange = { vm.setCloseThreshold(it); vm.refresh() },
                    onAutoCheckInChange = vm::setAutoCheckIn,
                    onHourChange = vm::setCheckInHour,
                    onRecipientsChange = vm::setRecipients,
                )
            }
        }
    }
}
