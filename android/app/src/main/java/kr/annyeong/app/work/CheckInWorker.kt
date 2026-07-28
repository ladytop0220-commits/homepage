package kr.annyeong.app.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import kr.annyeong.app.data.CommsManager
import kr.annyeong.app.data.MessageTemplates
import kr.annyeong.app.data.SettingsStore
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * 매일 정해진 시각에 등록된 가족/친구에게 "잘 지내요" 안부 문자를 자동 발송한다.
 * (설정에서 켠 경우에만 동작)
 */
class CheckInWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val settings = SettingsStore(applicationContext)
        if (!settings.autoCheckIn.first()) return Result.success()

        val recipients = settings.recipients.first()
        if (recipients.isEmpty()) return Result.success()

        val name = settings.senderName.first()
        val message = MessageTemplates.quickSafe(name)
        val comms = CommsManager(applicationContext)

        recipients.forEach { number ->
            comms.sendSmsDirect(number, message)
        }
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "annyeong_daily_checkin"

        /** 매일 지정한 시각(hour)에 실행되도록 예약. */
        fun schedule(context: Context, hour: Int) {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(now)) add(Calendar.DAY_OF_MONTH, 1)
            }
            val initialDelay = target.timeInMillis - now.timeInMillis

            val request = PeriodicWorkRequestBuilder<CheckInWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request,
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
