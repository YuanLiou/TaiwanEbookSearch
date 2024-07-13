package liou.rayyuan.ebooksearchtaiwan.utils

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.R

class FeatureDeliveryHelper(
    context: Context
) {
    private val manager: SplitInstallManager = SplitInstallManagerFactory.create(context.applicationContext)
    private val moduleBarcodeScanner by lazy { context.getString(R.string.module_feature_barcode_scanner) }
    private var callback: FeatureDeliveryCallback? = null
    private val listener =
        SplitInstallStateUpdatedListener { state ->
            val moduleNames = state.moduleNames().joinToString(" - ")
            when (state.status()) {
                SplitInstallSessionStatus.DOWNLOADING -> {
                    val progress = state.bytesDownloaded().toInt()
                    val max = state.totalBytesToDownload().toInt()
                    val message = context.getString(R.string.downloading_module, moduleNames)
                    callback?.showMessage("$message, $progress/$max")
                }

                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                    callback?.let {
                        manager.startConfirmationDialogForResult(state, it.provideConfirmationDialogResultLauncher())
                    }
                }

                SplitInstallSessionStatus.INSTALLED -> {
                    callback?.moduleInstallSuccess()
                    loadAndLaunchModule(moduleNames)
                }

                SplitInstallSessionStatus.INSTALLING -> {
                    val progress = state.bytesDownloaded().toInt()
                    val max = state.totalBytesToDownload().toInt()
                    val message = context.getString(R.string.installing_module, moduleNames)
                    callback?.showMessage("$message, $progress/$max")
                }

                SplitInstallSessionStatus.FAILED -> {
                    callback?.showMessage(
                        context.getString(R.string.error_for_installing_module, state.errorCode(), state.moduleNames())
                    )
                }

                SplitInstallSessionStatus.CANCELED -> Unit

                SplitInstallSessionStatus.CANCELING -> Unit

                SplitInstallSessionStatus.DOWNLOADED -> Unit

                SplitInstallSessionStatus.PENDING -> Unit

                SplitInstallSessionStatus.UNKNOWN -> Unit
            }
        }

    fun startListeningInstallationCallback(callback: FeatureDeliveryCallback) {
        this.callback = callback
        manager.registerListener(listener)
    }

    fun stopListeningInstallationCallback() {
        this.callback = null
        manager.unregisterListener(listener)
    }

    fun loadAndLaunchBarcodeScanner() {
        loadAndLaunchModule(moduleBarcodeScanner)
    }

    private fun loadAndLaunchModule(moduleName: String) {
        val className =
            when (moduleName) {
                moduleBarcodeScanner -> BARCODE_SCANNER_CLASSNAME
                else -> return
            }

        if (manager.installedModules.contains(moduleName)) {
            val intent = Intent().setClassName(BuildConfig.APPLICATION_ID, className)
            callback?.launchIntent(intent)
        }

        val request =
            SplitInstallRequest.newBuilder()
                .addModule(moduleName)
                .build()
        manager.startInstall(request)
    }

    interface FeatureDeliveryCallback {
        fun launchIntent(intent: Intent)

        fun provideConfirmationDialogResultLauncher(): ActivityResultLauncher<IntentSenderRequest>

        fun showMessage(message: String)

        fun moduleInstallSuccess()
    }

    companion object {
        private const val PACKAGE_NAME = "liou.rayyuan.ebooksearchtaiwan"

        // liou.rayyuan.ebooksearchtaiwan.camerapreview.CameraPreviewActivity
        private const val BARCODE_SCANNER_CLASSNAME = "$PACKAGE_NAME.camerapreview.CameraPreviewActivity"
    }
}
