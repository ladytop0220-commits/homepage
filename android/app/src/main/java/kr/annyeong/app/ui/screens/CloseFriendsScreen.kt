package kr.annyeong.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kr.annyeong.app.data.ContactFrequency
import kr.annyeong.app.ui.theme.AnnyeongCall

/**
 * 가까운 친구 화면 — 최근 한 달간 자주 연락한 사람을 자동으로 보여준다.
 * "연락처에 등록" 버튼으로 이들을 폰 즐겨찾기(별표)에 한번에 추가한다.
 */
@Composable
fun CloseFriendsScreen(
    closeFriends: List<ContactFrequency>,
    threshold: Int,
    loading: Boolean,
    onCall: (kr.annyeong.app.data.Contact) -> Unit,
    onRegisterAll: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        ScreenTitle("가까운 친구", modifier = Modifier.padding(vertical = 12.dp))
        Text(
            "최근 한 달간 $threshold 번 이상 전화·문자를 주고받은 분들이에요.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(12.dp))

        if (loading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.Center,
            ) { CircularProgressIndicator() }
        }

        if (!loading && closeFriends.isEmpty()) {
            Text(
                "아직 자주 연락한 기록이 없어요.\n전화나 문자를 주고받으면 여기에 모여요.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 24.dp),
            )
        }

        if (closeFriends.isNotEmpty()) {
            Button(
                onClick = onRegisterAll,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
            ) {
                Icon(Icons.Filled.PersonAdd, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("모두 연락처 즐겨찾기에 추가", style = MaterialTheme.typography.labelLarge)
            }
            Spacer(Modifier.height(12.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(closeFriends, key = { it.contact.id }) { cf ->
                FriendRow(cf, onCall)
            }
        }
    }
}

@Composable
private fun FriendRow(cf: ContactFrequency, onCall: (kr.annyeong.app.data.Contact) -> Unit) {
    Card(
        onClick = { onCall(cf.contact) },
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
            InitialAvatar(cf.contact.initial)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    cf.contact.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "전화 ${cf.callCount}회 · 문자 ${cf.smsCount}회",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Icon(
                imageVector = Icons.Filled.Phone,
                contentDescription = "전화 걸기",
                tint = AnnyeongCall,
                modifier = Modifier.width(44.dp).height(44.dp),
            )
        }
    }
}
