package liou.rayyuan.ebooksearchtaiwan.camerapreview

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import liou.rayyuan.ebooksearchtaiwan.di.barcodeScannerModules
import liou.rayyuan.ebooksearchtaiwan.camerapreview.permission.CameraPermissionScreen
import liou.rayyuan.ebooksearchtaiwan.camerapreview.preview.CameraPreviewScreen
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.compose.getKoin

@Composable
fun BarcodeScanner(
    modifier: Modifier = Modifier,
    onRequestWindowColorMode: (colorMode: Int) -> Unit = {},
    onScanBarcode: (barcode: String) -> Unit = {},
    onClickBarcodeResult: (barcode: String) -> Unit = {}
) {
    KoinAndroidContext {
        getKoin().loadModules(barcodeScannerModules)
        Scaffold(
            containerColor = Color.Black,
            contentColor = Color.Black,
            modifier = modifier.fillMaxSize()
        ) { paddings ->
            BarcodeScannerNavHost(
                modifier = Modifier.padding(paddings),
                onRequestWindowColorMode = onRequestWindowColorMode,
                onScanBarcode = onScanBarcode,
                onClickBarcodeResult = onClickBarcodeResult
            )
        }
    }
}

@Composable
private fun BarcodeScannerNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    onRequestWindowColorMode: (colorMode: Int) -> Unit = {},
    onScanBarcode: (barcode: String) -> Unit = {},
    onClickBarcodeResult: (barcode: String) -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = BarcodeScannerRoute.PERMISSIONS_ROUTE,
        modifier = modifier
    ) {
        composable(BarcodeScannerRoute.PERMISSIONS_ROUTE) {
            CameraPermissionScreen(
                onNavigateToPreview = {
                    navController.navigate(BarcodeScannerRoute.PREVIEW_ROUTE) {
                        popUpTo(0)
                    }
                },
                modifier = Modifier
            )
        }
        composable(BarcodeScannerRoute.PREVIEW_ROUTE) {
            val context = LocalContext.current
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                navController.navigate(BarcodeScannerRoute.PERMISSIONS_ROUTE) {
                    popUpTo(0)
                }
            }

            CameraPreviewScreen(
                onRequestWindowColorMode = onRequestWindowColorMode,
                onScanBarcode = onScanBarcode,
                onClickBarcodeResult = onClickBarcodeResult
            )
        }
    }
}

object BarcodeScannerRoute {
    const val PREVIEW_ROUTE = "preview"
    const val PERMISSIONS_ROUTE = "permissions"
}
