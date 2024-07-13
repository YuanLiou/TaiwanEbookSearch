package liou.rayyuan.ebooksearchtaiwan.camerapreview

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.camerapreview.databinding.ActivityCameraPreviewBinding
import liou.rayyuan.ebooksearchtaiwan.R as AppR

class CameraPreviewActivity : BaseActivity(R.layout.activity_camera_preview) {
    private var _viewBinding: ActivityCameraPreviewBinding? = null
    private val binding get() = _viewBinding!!

    private val cameraPermissionRequestCode = 1001

    private lateinit var manualEnablePermissionsLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _viewBinding = ActivityCameraPreviewBinding.bind(findViewById(R.id.activity_camera_preview_rootView))

        binding.activityCameraPreviewAuthText.setOnClickListener {
            requestCameraPermission()
        }

        if (shouldRequestCameraPermission()) {
            requestCameraPermission()
            binding.scannerStatusView.text = getString(R.string.camera_permission_waiting)
        } else {
            startDecodeBarcode(savedInstanceState)
        }

        manualEnablePermissionsLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (shouldRequestCameraPermission()) {
                    requestCameraPermission()
                } else {
                    readyToShowCameraView()
                }
            }
    }

    private fun startDecodeBarcode(savedInstanceState: Bundle?) {
    }

    override fun onDestroy() {
        _viewBinding = null
        super.onDestroy()
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraPermissionRequestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            cameraPermissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readyToShowCameraView()
                    startDecodeBarcode(null)
                } else {
                    binding.activityCameraPreviewAuthText.visibility = View.VISIBLE
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        MaterialAlertDialogBuilder(this)
                            .setTitle(R.string.permission_request_title)
                            .setMessage(R.string.permission_required_camera)
                            .setPositiveButton(R.string.dialog_auth) { _, _ -> requestCameraPermission() }
                            .setNegativeButton(R.string.dialog_cancel) { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }
                            .create().show()
                    } else {
                        val appName = getString(AppR.string.app_name)
                        val permissionName = getString(R.string.permission_camera_name)
                        val authYourselfMessage = getString(R.string.auth_yourself, appName, permissionName)

                        MaterialAlertDialogBuilder(this)
                            .setTitle(R.string.permission_request_title)
                            .setMessage(authYourselfMessage)
                            .setNegativeButton(AppR.string.dialog_ok) { dialogInterface, _ -> dialogInterface.dismiss() }
                            .setPositiveButton(R.string.auth_take_me_there) { _, _ -> openApplicationSetting() }
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
        manualEnablePermissionsLauncher.launch(intent)
    }

    private fun shouldRequestCameraPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED

    private fun runWithCameraPermission(action: () -> Unit) {
        if (!shouldRequestCameraPermission()) {
            action()
        }
    }

    private fun readyToShowCameraView() {
        binding.scannerStatusView.text = getString(R.string.scanner_default_status)
        binding.activityCameraPreviewAuthText.visibility = View.INVISIBLE
    }
}
