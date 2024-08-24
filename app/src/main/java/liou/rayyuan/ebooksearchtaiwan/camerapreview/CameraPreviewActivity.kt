package liou.rayyuan.ebooksearchtaiwan.camerapreview

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.os.bundleOf
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchActivity
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

class CameraPreviewActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EBookTheme(
                darkTheme = isDarkTheme()
            ) {
                BarcodeScanner(
                    onRequestWindowColorMode = { colorMode ->
                        // Window color mode APIs require API level 26+
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            window?.colorMode = colorMode
                        }
                    },
                    onBarcodeAvailable = { barcode ->
                        val resultIntent = intent
                        val bundle = bundleOf(BookSearchActivity.KEY_BARCODE_RESULT to barcode)
                        resultIntent.putExtras(bundle)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                )
            }
        }
    }
}
