package liou.rayyuan.ebooksearchtaiwan.camerapreview

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.CaptureManager
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.databinding.ActivityCameraPreviewBinding
import liou.rayyuan.ebooksearchtaiwan.utils.bindView

class CameraPreviewActivity : AppCompatActivity() {

    companion object {
        const val resultISBNTextKey = "result-isbn-text-key"
    }

    private lateinit var viewBinding: ActivityCameraPreviewBinding
    private val statusText: TextView by bindView(R.id.activity_camera_preview_status_text)
    private val scanningProgressBar: ProgressBar by bindView(R.id.activity_camera_preview_progressbar)
    private val scanningResultTitle: TextView by bindView(R.id.activity_camera_preview_result_title)
    private val authText: TextView by bindView(R.id.activity_camera_preview_auth_text)

    private val cameraPermissionRequestCode = 1001
    private val cameraPermissionManuallyEnable = 1002

    private lateinit var captureManager: CaptureManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraPreviewBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        captureManager = CaptureManager(this, viewBinding.zxingBarcodeScanner)
        captureManager.initializeFromIntent(intent, savedInstanceState)

        authText.setOnClickListener {
            requestCameraPermission()
        }

        if (shouldRequestCameraPermission()) {
            requestCameraPermission()
            scanningProgressBar.visibility = View.GONE
            scanningResultTitle.text = getString(R.string.camera_permission_waiting)
            captureManager.setShowMissingCameraPermissionDialog(true)
        } else {
            captureManager.decode()
        }
    }

    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        captureManager.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            cameraPermissionManuallyEnable -> {
                if (shouldRequestCameraPermission()) {
                    requestCameraPermission()
                    captureManager.setShowMissingCameraPermissionDialog(true)
                } else {
                    readyToShowCameraView()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraPermissionRequestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        captureManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        statusText.visibility = View.GONE
        scanningProgressBar.visibility = View.VISIBLE
        scanningResultTitle.text = getString(R.string.camera_scanning_result)
        authText.visibility = View.INVISIBLE
        captureManager.decode()
    }
}