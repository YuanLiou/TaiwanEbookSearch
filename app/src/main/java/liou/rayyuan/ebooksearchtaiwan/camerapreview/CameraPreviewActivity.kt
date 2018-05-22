package liou.rayyuan.ebooksearchtaiwan.camerapreview

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.view.widget.AutoFitTextureView
import java.nio.ByteBuffer

class CameraPreviewActivity : AppCompatActivity(), CameraPreviewManager.OnCameraPreviewCallback,
        CameraPreviewManager.OnDisplaySizeRequireHandler {
    private val statusText: TextView by bindView(R.id.activity_camera_preview_status_text)
    private val cameraView: AutoFitTextureView by bindView(R.id.activity_camera_preview_mainview)

    private lateinit var cameraPreviewManager: CameraPreviewManager

    private val cameraPermissionRequestCode = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_preview)

        cameraPreviewManager = CameraPreviewManager(applicationContext, cameraView, this, this)
        cameraPreviewManager.setupLifeCycleOwner(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
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

    override fun onByteBufferGenerated(buffer: ByteBuffer?) {
    }
    //endregion

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraPermissionRequestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            cameraPermissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraView.visibility = View.VISIBLE
                    statusText.visibility = View.GONE
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        AlertDialog.Builder(this)
                                .setTitle(R.string.permission_request_title)
                                .setMessage(R.string.permission_required_camera)
                                .setPositiveButton(R.string.dialog_auth, { _, _ ->  requestCameraPermission() })
                                .setNegativeButton(R.string.dialog_cancel, {
                                    dialogInterface, _ -> dialogInterface.dismiss()
                                })
                                .create().show()
                    } else {
                        val appName = getString(R.string.app_name)
                        val permissionName = getString(R.string.permission_camera_name)
                        val authYourselfMessage = getString(R.string.auth_yourself, appName, permissionName)

                        AlertDialog.Builder(this)
                                .setTitle(R.string.permission_request_title)
                                .setMessage(authYourselfMessage)
                                .setPositiveButton(R.string.dialog_ok, { dialogInterface, _ -> dialogInterface.dismiss() })
                                .create().show()
                    }
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun <T: View> Activity.bindView(@IdRes id: Int): Lazy<T> =
            lazy { findViewById<T>(id) }
}