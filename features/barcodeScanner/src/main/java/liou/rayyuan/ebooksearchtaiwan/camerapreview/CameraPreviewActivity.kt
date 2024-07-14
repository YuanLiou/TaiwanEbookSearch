package liou.rayyuan.ebooksearchtaiwan.camerapreview

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

class CameraPreviewActivity : BaseActivity(R.layout.activity_camera_preview) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EBookTheme {
                Text("Hello World")
            }
        }
    }
}
