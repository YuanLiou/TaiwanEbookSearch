package liou.rayyuan.ebooksearchtaiwan.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import java.util.concurrent.TimeUnit
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.R

class FeatureDeliveryHelper(
    context: Context,
    private val resourceHelper: ResourceHelper
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
                    callback?.provideConfirmationDialogResultLauncher()?.let { launcher ->
                        manager.startConfirmationDialogForResult(state, launcher)
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

    private var previousLaunchModuleName: String = ""
    private var previousLaunchTimeStamps: Long = 0

    fun startListeningInstallationCallback(callback: FeatureDeliveryCallback) {
        this.callback = callback
        manager.registerListener(listener)
    }

    fun stopListeningInstallationCallback() {
        this.callback = null
        manager.unregisterListener(listener)
    }

    fun isBarcodeScannerInstalled(): Boolean = manager.installedModules.contains(moduleBarcodeScanner)

    fun loadAndLaunchBarcodeScanner() {
        loadAndLaunchModule(moduleBarcodeScanner)
    }

    fun uninstallAllModules(onSuccess: (() -> Unit)? = null) {
        val installedModules = manager.installedModules.toList()
        manager.deferredUninstall(installedModules)
            .addOnSuccessListener {
                onSuccess?.invoke()
                callback?.showMessage(resourceHelper.getString(R.string.uninstalling_module, installedModules.joinToString(",")))
            }.addOnFailureListener {
                callback?.showMessage(resourceHelper.getString(R.string.uninstalling_module_failed, installedModules.joinToString(",")))
            }
    }

    private fun loadAndLaunchModule(moduleName: String) {
        // FIX: To prevent sometimes feature delivery will launch multiple instances
        val currentTimeStamps = System.currentTimeMillis()
        val delta = currentTimeStamps - previousLaunchTimeStamps
        val thresholds = TimeUnit.SECONDS.toMillis(2L)
        if (moduleName == previousLaunchModuleName && delta < thresholds) {
            Log.w(TAG, "prevent launch duplicated module")
            return
        }
        previousLaunchModuleName = moduleName
        previousLaunchTimeStamps = currentTimeStamps

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

        fun provideConfirmationDialogResultLauncher(): ActivityResultLauncher<IntentSenderRequest>?

        fun showMessage(message: String)

        fun moduleInstallSuccess()
    }

    open class FeatureDeliveryCallbackAdapter : FeatureDeliveryCallback {
        override fun launchIntent(intent: Intent) = Unit

        override fun provideConfirmationDialogResultLauncher(): ActivityResultLauncher<IntentSenderRequest>? = null

        override fun showMessage(message: String) = Unit

        override fun moduleInstallSuccess() = Unit
    }

    companion object {
        private const val PACKAGE_NAME = "liou.rayyuan.ebooksearchtaiwan"
        private const val TAG = "FeatureDeliveryHelper"

        // liou.rayyuan.ebooksearchtaiwan.camerapreview.CameraPreviewActivity
        private const val BARCODE_SCANNER_CLASSNAME = "$PACKAGE_NAME.camerapreview.CameraPreviewActivity"
    }
}
