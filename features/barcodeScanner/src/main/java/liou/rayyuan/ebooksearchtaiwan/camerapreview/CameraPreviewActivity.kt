package liou.rayyuan.ebooksearchtaiwan.camerapreview

import android.os.Bundle
import androidx.activity.compose.setContent
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

class CameraPreviewActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EBookTheme(
                darkTheme = isDarkTheme()
            ) {
                BarcodeScanner()
            }
        }
    }
}
