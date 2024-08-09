package liou.rayyuan.ebooksearchtaiwan.camerapreview.di

import liou.rayyuan.ebooksearchtaiwan.camerapreview.preview.CameraPreviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val barcodeScannerModule =
    module {
        viewModel {
            CameraPreviewViewModel()
        }
    }

val barcodeScannerModules = listOf(barcodeScannerModule)
