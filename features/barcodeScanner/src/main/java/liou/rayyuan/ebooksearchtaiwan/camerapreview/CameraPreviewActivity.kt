package liou.rayyuan.ebooksearchtaiwan.camerapreview

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

class CameraPreviewActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EBookTheme(
                darkTheme = isDarkTheme()
            ) {
                Scaffold(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    containerColor = EBookTheme.colors.colorBackground
                ) { paddings ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier =
                            Modifier
                                .padding(paddings)
                                .fillMaxSize()
                    ) {
                        Text(
                            "Hello World",
                            style =
                                TextStyle.Default.copy(
                                    color = EBookTheme.colors.textColorTertiary,
                                    fontSize = 24.sp
                                )
                        )
                    }
                }
            }
        }
    }
}
