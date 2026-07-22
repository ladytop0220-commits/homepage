package kr.annyeong.app.data

import java.util.Calendar

/**
 * 어르신의 일상/건강 상태를 바탕으로 가족·친구에게 보낼
 * 안부 문자를 자동으로 만들어 주는 생성기.
 *
 * 개인정보가 외부로 나가지 않도록, 문장 생성은 전적으로 기기 안에서 이뤄진다.
 */
object MessageTemplates {

    enum class Mood(val label: String, val phrase: String) {
        GOOD("좋음", "오늘은 기분이 아주 좋아요"),
        OKAY("보통", "오늘은 그럭저럭 잘 지내고 있어요"),
        TIRED("피곤", "오늘은 조금 피곤하지만 괜찮아요"),
        LONELY("적적함", "오늘은 조금 적적하네요. 목소리가 그리워요"),
    }

    /** 오늘 한 일/상태 체크 항목 */
    enum class Activity(val label: String, val phrase: String) {
        MEAL("식사 잘함", "밥도 잘 챙겨 먹었어요"),
        WALK("산책함", "동네 한 바퀴 산책도 다녀왔어요"),
        MEDICINE("약 챙겨먹음", "약도 잊지 않고 챙겨 먹었어요"),
        SLEEP_WELL("잘 잤음", "어젯밤 푹 잘 잤어요"),
        HOSPITAL("병원 다녀옴", "오늘 병원에 다녀왔어요"),
        FRIENDS("친구 만남", "오랜만에 친구도 만났어요"),
    }

    private fun greeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..10 -> "좋은 아침이에요"
            in 11..16 -> "잘 지내고 있나요"
            in 17..21 -> "좋은 저녁이에요"
            else -> "늦은 시간에 미안해요"
        }
    }

    /**
     * 상태를 조합해 자연스러운 안부 문자를 만든다.
     * @param senderName 보내는 사람(어르신) 이름 — 서명에 사용
     * @param note 직접 덧붙일 한마디(선택)
     */
    fun buildCheckIn(
        mood: Mood,
        activities: List<Activity>,
        senderName: String,
        note: String = "",
    ): String {
        val sb = StringBuilder()
        sb.append("${greeting()}. ")
        sb.append("${mood.phrase}. ")
        // 각 활동 문구는 그 자체로 완결된 문장(…요)이라 그대로 이어 붙인다.
        activities.forEach { sb.append("${it.phrase}. ") }
        if (note.isNotBlank()) sb.append("${note.trim()} ")
        sb.append("걱정 말고 잘 지내요. ")
        if (senderName.isNotBlank()) sb.append("- $senderName 드림")
        return sb.toString().trim()
    }

    /** 간단 버전: 버튼 하나로 보내는 "나 잘 있어요" 안심 문자 */
    fun quickSafe(senderName: String): String {
        val name = if (senderName.isNotBlank()) " -$senderName" else ""
        return "${greeting()}. 저는 오늘도 건강하게 잘 지내고 있어요. 걱정 말아요.$name"
    }

    /** 미리 준비된 상황별 문구 (탭 한 번으로 전송) */
    val presets: List<Pair<String, (String) -> String>> = listOf(
        "안부 인사" to { name -> quickSafe(name) },
        "보고 싶어요" to { name ->
            "요즘 목소리가 참 그립네요. 시간 되면 전화 한 통 주세요.${if (name.isNotBlank()) " -$name" else ""}"
        },
        "밥 잘 챙겨요" to { name ->
            "끼니 거르지 말고 밥 잘 챙겨 드세요. 저도 잘 챙겨 먹고 있어요.${if (name.isNotBlank()) " -$name" else ""}"
        },
        "고마워요" to { name ->
            "늘 챙겨줘서 고마워요. 덕분에 든든하게 잘 지내요.${if (name.isNotBlank()) " -$name" else ""}"
        },
    )
}
