package liou.rayyuan.ebooksearchtaiwan.ui.composables

import android.content.Context
import android.view.OrientationEventListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalInspectionMode

@Composable
fun DeviceOrientationListener(
    applicationContext: Context,
    onOrientationChangeRawValue: (orientationValue: Int) -> Unit = {},
    onOrientationChange: (orientation: DeviceOrientation) -> Unit
) {
    if (LocalInspectionMode.current) {
        return
    }

    DisposableEffect(key1 = Unit) {
        val orientationListener =
            object : OrientationEventListener(applicationContext) {
                override fun onOrientationChanged(orientation: Int) {
                    if (orientation == ORIENTATION_UNKNOWN) {
                        return
                    }

                    if (orientation >= 350 || orientation < 10) {
                        onOrientationChange(DeviceOrientation.Portrait)
                    } else if (orientation in 80..159) {
                        onOrientationChange(DeviceOrientation.ReverseLandscape)
                    } else if (orientation in 200..289) {
                        onOrientationChange(DeviceOrientation.Landscape)
                    }
                    onOrientationChangeRawValue(orientation)
                }
            }
        orientationListener.enable()

        onDispose {
            orientationListener.disable()
        }
    }
}
