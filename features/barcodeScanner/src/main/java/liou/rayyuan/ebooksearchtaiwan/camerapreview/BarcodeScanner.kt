package liou.rayyuan.ebooksearchtaiwan.camerapreview

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import liou.rayyuan.ebooksearchtaiwan.camerapreview.di.barcodeScannerModules
import liou.rayyuan.ebooksearchtaiwan.camerapreview.permission.CameraPermissionScreen
import liou.rayyuan.ebooksearchtaiwan.camerapreview.preview.CameraPreviewScreen
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.compose.getKoin
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun BarcodeScanner(modifier: Modifier = Modifier) {
    KoinAndroidContext {
        getKoin().loadModules(barcodeScannerModules)
        Scaffold(
            containerColor = EBookTheme.colors.colorBackground,
            contentColor = EBookTheme.colors.colorOnPrimary,
            modifier = modifier.fillMaxSize()
        ) { paddings ->
            BarcodeScannerNavHost(
                modifier = Modifier.padding(paddings)
            )
        }
    }
}

@Composable
private fun BarcodeScannerNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
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

            CameraPreviewScreen()
        }
    }
}

object BarcodeScannerRoute {
    const val PREVIEW_ROUTE = "preview"
    const val PERMISSIONS_ROUTE = "permissions"
}
