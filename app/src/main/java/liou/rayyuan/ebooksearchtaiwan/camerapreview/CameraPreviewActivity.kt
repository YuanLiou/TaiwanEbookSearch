package liou.rayyuan.ebooksearchtaiwan.camerapreview

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.os.bundleOf
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchActivity
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.utils.DeviceVibrateHelper
import org.koin.android.ext.android.inject

class CameraPreviewActivity : BaseActivity() {
    private val deviceVibrateHelper: DeviceVibrateHelper by inject()
    private var scannedBarcode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            if (savedInstanceState.getString(KEY_SCANNED_BARCODE) != null) {
                scannedBarcode = savedInstanceState.getString(KEY_SCANNED_BARCODE, "") ?: ""
            }
        }

        setContent {
            EBookTheme(
                darkTheme = isDarkTheme()
            ) {
                BarcodeScanner(
                    onRequestWindowColorMode = { colorMode ->
                        // Window color mode APIs require API level 26+
                        window?.colorMode = colorMode
                    },
                    onScanBarcode = { barcode ->
                        if (scannedBarcode != barcode) {
                            deviceVibrateHelper.vibrate()
                            scannedBarcode = barcode
                        }
                    },
                    onClickBarcodeResult = { barcode ->
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SCANNED_BARCODE, scannedBarcode)
    }

    companion object {
        private const val KEY_SCANNED_BARCODE = "key-scanned-barcode"
    }
}
