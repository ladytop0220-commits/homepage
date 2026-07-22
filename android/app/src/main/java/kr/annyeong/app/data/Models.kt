package kr.annyeong.app.data

/** 폰 연락처의 한 사람 */
data class Contact(
    val id: Long,
    val name: String,
    val phone: String,
    val starred: Boolean = false,
) {
    /** 화면에서 아바타로 쓸 이름 첫 글자 */
    val initial: String get() = name.trim().firstOrNull()?.toString() ?: "?"
}

/**
 * 최근 한 달간 연락(전화/문자) 빈도 분석 결과.
 * "한 달에 한 번 이상 연락한 친구" 자동 선별에 사용.
 */
data class ContactFrequency(
    val contact: Contact,
    val callCount: Int,
    val smsCount: Int,
    val lastContactMillis: Long,
) {
    val totalCount: Int get() = callCount + smsCount

    /** 기준(한 달 threshold 회) 이상이면 '가까운 친구' */
    fun isCloseFriend(threshold: Int): Boolean = totalCount >= threshold
}
