package kr.annyeong.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kr.annyeong.app.data.Contact
import kr.annyeong.app.ui.theme.AnnyeongCall

/** 전화 걸기 화면 — 즐겨찾기 친구를 위로, 이름 검색 가능. 행을 누르면 바로 전화. */
@Composable
fun CallScreen(
    contacts: List<Contact>,
    onCall: (Contact) -> Unit,
) {
    var query by remember { mutableStateOf("") }

    val filtered = remember(contacts, query) {
        contacts
            .filter { query.isBlank() || it.name.contains(query, ignoreCase = true) }
            .sortedWith(compareByDescending<Contact> { it.starred }.thenBy { it.name })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        ScreenTitle("전화 걸기", modifier = Modifier.padding(vertical = 12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("이름 검색", style = MaterialTheme.typography.bodyLarge) },
            textStyle = MaterialTheme.typography.bodyLarge,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.padding(6.dp))

        if (filtered.isEmpty()) {
            Text(
                "연락처가 없어요.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 24.dp),
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 8.dp),
        ) {
            items(filtered, key = { it.id }) { contact ->
                CallRow(contact, onCall)
            }
        }
    }
}

@Composable
private fun CallRow(contact: Contact, onCall: (Contact) -> Unit) {
    Card(
        onClick = { onCall(contact) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            InitialAvatar(contact.initial)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        contact.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    if (contact.starred) {
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "가까운 친구",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
                Text(contact.phone, style = MaterialTheme.typography.bodyMedium)
            }
            Icon(
                imageVector = Icons.Filled.Phone,
                contentDescription = "전화 걸기",
                tint = AnnyeongCall,
                modifier = Modifier.size(44.dp),
            )
        }
    }
}
