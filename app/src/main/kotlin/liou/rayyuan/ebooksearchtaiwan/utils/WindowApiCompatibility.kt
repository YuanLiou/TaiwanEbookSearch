package liou.rayyuan.ebooksearchtaiwan.utils

import android.os.Build

internal fun isWindowInsetsRuntimeCompatible(
    sdkInt: Int,
    methodExists: () -> Boolean,
): Boolean {
    if (sdkInt < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        return true
    }

    return try {
        methodExists()
    } catch (_: LinkageError) {
        false
    }
}

internal object WindowApiCompatibility {
    val hasCompatibleWindowInsetsRuntime: Boolean by lazy {
        isWindowInsetsRuntimeCompatible(Build.VERSION.SDK_INT) {
            try {
                Class.forName("android.view.WindowInsets\$Type").getMethod("systemOverlays")
                true
            } catch (_: ReflectiveOperationException) {
                false
            }
        }
    }
}
