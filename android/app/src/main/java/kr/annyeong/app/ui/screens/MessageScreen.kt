package kr.annyeong.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kr.annyeong.app.data.Contact
import kr.annyeong.app.data.MessageTemplates
import kr.annyeong.app.ui.theme.AnnyeongMessage

/**
 * 안부 보내기 화면.
 * ① 받는 사람 고르기 → ② 상황 버튼(프리셋) 또는 오늘 상태로 문자 만들기 → ③ 보내기
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MessageScreen(
    contacts: List<Contact>,
    senderName: String,
    onSend: (recipients: List<Contact>, message: String) -> Unit,
) {
    val selected = remember { mutableStateListOf<Contact>() }
    var message by remember { mutableStateOf("") }

    var mood by remember { mutableStateOf(MessageTemplates.Mood.GOOD) }
    val activities = remember { mutableStateListOf<MessageTemplates.Activity>() }
    var note by remember { mutableStateOf("") }

    // 받는 사람 후보 : 즐겨찾기(가까운 친구) 우선
    val candidates = remember(contacts) {
        contacts.sortedWith(compareByDescending<Contact> { it.starred }.thenBy { it.name })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        ScreenTitle("안부 보내기")

        // ---------- ① 받는 사람 ----------
        Text("받는 사람", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        if (candidates.isEmpty()) {
            Text("연락처가 없어요.", style = MaterialTheme.typography.bodyMedium)
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            candidates.take(30).forEach { c ->
                val isSel = selected.contains(c)
                FilterChip(
                    selected = isSel,
                    onClick = { if (isSel) selected.remove(c) else selected.add(c) },
                    label = { Text(c.name, style = MaterialTheme.typography.bodyMedium) },
                    colors = FilterChipDefaults.filterChipColors(),
                )
            }
        }
        Text(
            if (selected.isEmpty()) "보낼 사람을 골라주세요" else "${selected.size}명 선택됨",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        HorizontalDivider(Modifier.padding(vertical = 4.dp))

        // ---------- ② 상황 버튼(프리셋) ----------
        Text("빠른 안부", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MessageTemplates.presets.forEach { (label, builder) ->
                OutlinedButton(onClick = { message = builder(senderName) }) {
                    Text(label, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        HorizontalDivider(Modifier.padding(vertical = 4.dp))

        // ---------- ② 오늘 상태로 만들기 ----------
        Text("오늘 나의 상태", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("기분", style = MaterialTheme.typography.bodyMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MessageTemplates.Mood.entries.forEach { m ->
                FilterChip(
                    selected = mood == m,
                    onClick = { mood = m },
                    label = { Text(m.label, style = MaterialTheme.typography.bodyMedium) },
                )
            }
        }
        Text("오늘 한 일", style = MaterialTheme.typography.bodyMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MessageTemplates.Activity.entries.forEach { a ->
                val on = activities.contains(a)
                FilterChip(
                    selected = on,
                    onClick = { if (on) activities.remove(a) else activities.add(a) },
                    label = { Text(a.label, style = MaterialTheme.typography.bodyMedium) },
                )
            }
        }
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("한마디 덧붙이기 (선택)") },
            textStyle = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedButton(
            onClick = {
                message = MessageTemplates.buildCheckIn(
                    mood = mood,
                    activities = activities.toList(),
                    senderName = senderName,
                    note = note,
                )
            },
        ) {
            Text("이 내용으로 문자 만들기", style = MaterialTheme.typography.bodyMedium)
        }

        HorizontalDivider(Modifier.padding(vertical = 4.dp))

        // ---------- ③ 미리보기 & 보내기 ----------
        Text("보낼 내용", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = message.ifBlank { "위에서 안부 내용을 골라주세요." },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp),
            )
        }

        Button(
            onClick = { onSend(selected.toList(), message) },
            enabled = selected.isNotEmpty() && message.isNotBlank(),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AnnyeongMessage),
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
            Spacer(Modifier.padding(6.dp))
            Text("문자 보내기", style = MaterialTheme.typography.labelLarge)
        }
        Spacer(Modifier.height(12.dp))
    }
}
