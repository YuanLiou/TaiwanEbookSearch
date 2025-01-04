package liou.rayyuan.ebooksearchtaiwan.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class DeviceVibrateHelper(
    private val context: Context
) {
    fun vibrate() {
        val vibrationEffect =
            VibrationEffect.createOneShot(
                VIBRATE_DURATION,
                VibrationEffect.DEFAULT_AMPLITUDE
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibrationManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibrationManager.defaultVibrator
            if (!vibrator.hasVibrator()) return
            vibrator.vibrate(vibrationEffect)
            return
        }

        // Version between Oreo (25) and S (31)
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (!vibrator.hasVibrator()) return
        vibrator.vibrate(vibrationEffect)
    }

    companion object {
        private const val VIBRATE_DURATION = 150L
    }
}
