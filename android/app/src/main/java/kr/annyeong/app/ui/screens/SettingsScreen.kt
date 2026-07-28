package kr.annyeong.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kr.annyeong.app.data.Contact

/** 설정 화면 — 내 이름, 가까운 친구 기준, 매일 자동 안부 문자 설정. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    contacts: List<Contact>,
    senderName: String,
    threshold: Int,
    autoCheckIn: Boolean,
    checkInHour: Int,
    recipients: List<String>,
    onNameChange: (String) -> Unit,
    onThresholdChange: (Int) -> Unit,
    onAutoCheckInChange: (Boolean) -> Unit,
    onHourChange: (Int) -> Unit,
    onRecipientsChange: (List<String>) -> Unit,
) {
    var name by remember(senderName) { mutableStateOf(senderName) }

    fun sameNumber(a: String, b: String) =
        a.filter { it.isDigit() }.takeLast(8) == b.filter { it.isDigit() }.takeLast(8)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        ScreenTitle("설정")

        // --- 내 이름 ---
        Text("내 이름", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("안부 문자 끝에 서명으로 들어가요.", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("예: 김순자") },
        )
        androidx.compose.material3.OutlinedButton(onClick = { onNameChange(name) }) {
            Text("이름 저장", style = MaterialTheme.typography.bodyMedium)
        }

        HorizontalDivider()

        // --- 가까운 친구 기준 ---
        Text("가까운 친구 기준", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            "한 달에 이 횟수 이상 연락한 사람을 가까운 친구로 봐요.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Stepper(
            value = threshold,
            suffix = "회",
            onMinus = { onThresholdChange((threshold - 1).coerceAtLeast(1)) },
            onPlus = { onThresholdChange((threshold + 1).coerceAtMost(30)) },
        )

        HorizontalDivider()

        // --- 매일 자동 안부 문자 ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "매일 자동 안부 문자",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "매일 정한 시각에 '잘 지내요' 문자를 자동으로 보내요.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Switch(checked = autoCheckIn, onCheckedChange = onAutoCheckInChange)
        }

        if (autoCheckIn) {
            Text("보내는 시각", style = MaterialTheme.typography.bodyMedium)
            Stepper(
                value = checkInHour,
                suffix = "시",
                onMinus = { onHourChange((checkInHour - 1).coerceAtLeast(0)) },
                onPlus = { onHourChange((checkInHour + 1).coerceAtMost(23)) },
            )

            Text("받는 사람 (자녀·가족)", style = MaterialTheme.typography.bodyMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                contacts.sortedByDescending { it.starred }.take(30).forEach { c ->
                    val isSel = recipients.any { sameNumber(it, c.phone) }
                    FilterChip(
                        selected = isSel,
                        onClick = {
                            val next = if (isSel) {
                                recipients.filterNot { sameNumber(it, c.phone) }
                            } else {
                                recipients + c.phone
                            }
                            onRecipientsChange(next)
                        },
                        label = { Text(c.name, style = MaterialTheme.typography.bodyMedium) },
                    )
                }
            }
            if (recipients.isEmpty()) {
                Text(
                    "받는 사람을 한 명 이상 골라주세요.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        HorizontalDivider()
        Text(
            "🔒 모든 정보(연락처·통화기록·설정)는 이 휴대폰 안에서만 처리되고, 외부로 전송되지 않습니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun Stepper(value: Int, suffix: String, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        FilledIconButton(onClick = onMinus, modifier = Modifier.size(56.dp)) {
            Icon(Icons.Filled.Remove, contentDescription = "줄이기")
        }
        Text(
            "$value$suffix",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        FilledIconButton(onClick = onPlus, modifier = Modifier.size(56.dp)) {
            Icon(Icons.Filled.Add, contentDescription = "늘리기")
        }
    }
}
