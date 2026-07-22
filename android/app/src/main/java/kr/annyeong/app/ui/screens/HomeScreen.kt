package kr.annyeong.app.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kr.annyeong.app.data.Contact
import kr.annyeong.app.ui.theme.AnnyeongCall
import kr.annyeong.app.ui.theme.AnnyeongGreenDark
import kr.annyeong.app.ui.theme.AnnyeongMessage

/**
 * 홈 화면 — 큰 버튼 4개.
 * 맨 위 "말로 전화 걸기"는 음성 인식으로 이름을 듣고 바로 전화를 건다.
 */
@Composable
fun HomeScreen(
    contacts: List<Contact>,
    senderName: String,
    hasCallPermission: Boolean,
    onCall: (Contact) -> Unit,
    onGoCall: () -> Unit,
    onGoMessage: () -> Unit,
    onGoFriends: () -> Unit,
    onGoSettings: () -> Unit,
) {
    val context = LocalContext.current

    // 음성 인식 결과 처리: 말한 문장 안에 이름이 들어있는 연락처를 찾아 전화
    val voiceLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
                .orEmpty()
            val match = contacts.firstOrNull { c ->
                spoken.contains(c.name) || c.name.contains(spoken.trim())
            }
            if (match != null) onCall(match)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = if (senderName.isNotBlank()) "$senderName 님, 안녕하세요" else "안녕하세요",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "무엇을 도와드릴까요?",
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.height(4.dp))

        BigButton(
            text = "말로 전화 걸기",
            subtitle = "\"○○에게 전화\" 라고 말해보세요",
            icon = Icons.Filled.Mic,
            color = AnnyeongGreenDark,
        ) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "누구에게 전화할까요?")
            }
            voiceLauncher.launch(intent)
        }

        BigButton(
            text = "전화 걸기",
            subtitle = "친구 목록에서 골라 전화",
            icon = Icons.Filled.Phone,
            color = AnnyeongCall,
            onClick = onGoCall,
        )

        BigButton(
            text = "안부 보내기",
            subtitle = "가족·친구에게 안부 문자",
            icon = Icons.Filled.MailOutline,
            color = AnnyeongMessage,
            onClick = onGoMessage,
        )

        BigButton(
            text = "가까운 친구",
            subtitle = "자주 연락하는 사람 모아보기",
            icon = Icons.Filled.Groups,
            color = MaterialTheme.colorScheme.primary,
            onClick = onGoFriends,
        )

        BigButton(
            text = "설정",
            subtitle = "이름·자동 안부 문자 설정",
            icon = Icons.Filled.Settings,
            color = MaterialTheme.colorScheme.onBackground,
            onClick = onGoSettings,
        )
    }
}
