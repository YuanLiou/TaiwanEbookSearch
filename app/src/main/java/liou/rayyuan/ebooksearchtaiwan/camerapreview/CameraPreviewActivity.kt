package liou.rayyuan.ebooksearchtaiwan.camerapreview

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Size
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.mlscanner.BarcodeVisionProcessor
import liou.rayyuan.ebooksearchtaiwan.mlscanner.FrameVisionProcessor
import liou.rayyuan.ebooksearchtaiwan.mlscanner.VisionProcessListener
import liou.rayyuan.ebooksearchtaiwan.utils.bindView
import liou.rayyuan.ebooksearchtaiwan.view.widget.AutoFitTextureView

class CameraPreviewActivity : AppCompatActivity(), CameraPreviewManager.OnCameraPreviewCallback,
        CameraPreviewManager.OnDisplaySizeRequireHandler, FrameVisionProcessor.CameraInformationCollector,
        VisionProcessListener {

    companion object {
        const val resultISBNTextKey = "result-isbn-text-key"
    }

    private val statusText: TextView by bindView(R.id.activity_camera_preview_status_text)
    private val cameraView: AutoFitTextureView by bindView(R.id.activity_camera_preview_mainview)
    private val scanningProgressBar: ProgressBar by bindView(R.id.activity_camera_preview_progressbar)
    private val scanningResultText: TextView by bindView(R.id.activity_camera_preview_result)
    private val scanningResultTitle: TextView by bindView(R.id.activity_camera_preview_result_title)
    private val authText: TextView by bindView(R.id.activity_camera_preview_auth_text)

    private lateinit var cameraPreviewManager: CameraPreviewManager
    private lateinit var frameVisionProcessor: FrameVisionProcessor

    private val cameraPermissionRequestCode = 1001
    private val cameraPermissionManuallyEnable = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_preview)

        cameraPreviewManager = CameraPreviewManager(applicationContext, cameraView, this, this)
        cameraPreviewManager.setupLifeCycleOwner(this)

        val barcodeVisionProcessor = BarcodeVisionProcessor().also {
            it.visionProcessListener = this
        }

        frameVisionProcessor = FrameVisionProcessor(barcodeVisionProcessor, this)
        frameVisionProcessor.setupLifecycleOwner(this)

        scanningResultText.setOnClickListener {
            val resultISBNText = scanningResultText.text.toString().trim()
            if (resultISBNText.isNotBlank()) {
                val intent = Intent().apply {
                    val bundle = Bundle()
                    bundle.putString(resultISBNTextKey, resultISBNText)
                    putExtras(bundle)
                }

                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

        authText.setOnClickListener {
            requestCameraPermission()
        }

        if (shouldRequestCameraPermission()) {
            requestCameraPermission()
            scanningProgressBar.visibility = View.GONE
            scanningResultTitle.text = getString(R.string.camera_permission_waiting)
        } else {
            frameVisionProcessor.start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            cameraPermissionManuallyEnable -> {
                if (shouldRequestCameraPermission()) {
                    requestCameraPermission()
                } else {
                    readyToShowCameraView()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    //region CameraPreviewManager.OnCameraPreviewFailureCallback
    override fun shouldRequestPermission() {
        cameraView.visibility = View.GONE
        statusText.visibility = View.VISIBLE
        statusText.text = getString(R.string.permission_required_camera)
    }

    override fun onError(message: String) {
        cameraView.visibility = View.GONE
        statusText.visibility = View.VISIBLE
        statusText.text = message
    }
    //endregion

    //region CameraPreviewManager.OnDisplaySizeRequireHandler
    override fun getDisplaySize(point: Point) = windowManager.defaultDisplay.getSize(point)

    override fun getDisplayOrientation(): Int = windowManager.defaultDisplay.rotation

    override fun onByteArrayGenerated(bytes: ByteArray?) {
        bytes?.run {
            frameVisionProcessor.setNextFrame(this)
        }
    }
    //endregion

    //region FrameVisionProcessor.CameraInformationCollector
    override fun getCameraPreviewSize(): Size = cameraPreviewManager.previewSize ?: Size(1280, 720)

    override fun getCameraOrientation(): Int = cameraPreviewManager.rotationConstraintDigit

    override fun getCameraFacingDirection(): Int = cameraPreviewManager.facing ?: 0
    //endregion

    //region VisionProcessListener
    override fun onVisionProcessSucceed(result: String) {
        scanningProgressBar.visibility = View.GONE
        scanningResultText.visibility = View.VISIBLE

        scanningResultText.text = result
    }
    //endregion

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraPermissionRequestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            cameraPermissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readyToShowCameraView()
                } else {
                    authText.visibility = View.VISIBLE
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        AlertDialog.Builder(this)
                                .setTitle(R.string.permission_request_title)
                                .setMessage(R.string.permission_required_camera)
                                .setPositiveButton(R.string.dialog_auth) { _, _ ->  requestCameraPermission() }
                                .setNegativeButton(R.string.dialog_cancel) { dialogInterface, _ -> dialogInterface.dismiss()
                                }
                                .create().show()
                    } else {
                        scanningResultTitle.text = getString(R.string.camera_permission_deny)
                        val appName = getString(R.string.app_name)
                        val permissionName = getString(R.string.permission_camera_name)
                        val authYourselfMessage = getString(R.string.auth_yourself, appName, permissionName)

                        AlertDialog.Builder(this)
                                .setTitle(R.string.permission_request_title)
                                .setMessage(authYourselfMessage)
                                .setNegativeButton(R.string.dialog_ok, { dialogInterface, _ -> dialogInterface.dismiss() })
                                .setPositiveButton(R.string.auth_take_me_there, { _, _ -> openApplicationSetting() })
                                .create().show()
                    }
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun openApplicationSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, cameraPermissionManuallyEnable)
    }

    private fun shouldRequestCameraPermission(): Boolean =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED

    private fun readyToShowCameraView() {
        cameraView.visibility = View.VISIBLE
        statusText.visibility = View.GONE
        scanningProgressBar.visibility = View.VISIBLE
        scanningResultTitle.text = getString(R.string.camera_scanning_result)
        authText.visibility = View.GONE
        frameVisionProcessor.start()
    }
}