package kr.annyeong.app.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager

/**
 * 실제 전화 걸기와 문자 보내기를 담당.
 * - 전화: CALL_PHONE 권한이 있으면 바로 발신(ACTION_CALL), 없으면 다이얼러를 열어줌(ACTION_DIAL)
 * - 문자: SEND_SMS 권한이 있으면 앱이 직접 발송, 아니면 문자앱을 채워서 열어줌
 */
class CommsManager(private val context: Context) {

    /** 권한이 있으면 즉시 전화, 없으면 다이얼러에 번호를 채워 연다. */
    fun call(phone: String, hasCallPermission: Boolean) {
        val uri = Uri.parse("tel:${phone.filter { it.isDigit() || it == '+' }}")
        val action = if (hasCallPermission) Intent.ACTION_CALL else Intent.ACTION_DIAL
        val intent = Intent(action, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /** SEND_SMS 권한으로 문자를 직접 발송한다. 성공 여부를 반환. */
    fun sendSmsDirect(phone: String, message: String): Boolean {
        return try {
            val smsManager = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }
            // 긴 문자는 자동으로 나눠서 발송
            val parts = smsManager.divideMessage(message)
            smsManager.sendMultipartTextMessage(phone, null, parts, null, null)
            true
        } catch (e: Exception) {
            false
        }
    }

    /** 권한 없이 문자앱을 열어 내용을 미리 채워 준다(사용자가 직접 '보내기'). */
    fun openSmsApp(phone: String, message: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phone")).apply {
            putExtra("sms_body", message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
