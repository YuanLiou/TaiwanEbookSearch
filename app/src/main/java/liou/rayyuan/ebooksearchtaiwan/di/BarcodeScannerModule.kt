package liou.rayyuan.ebooksearchtaiwan.di

import liou.rayyuan.ebooksearchtaiwan.camerapreview.preview.CameraPreviewViewModel
import liou.rayyuan.ebooksearchtaiwan.camerapreview.usecase.CameraUseCase
import liou.rayyuan.ebooksearchtaiwan.camerapreview.usecase.CameraXUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val barcodeScannerModule =
    module {
        viewModel {
            CameraPreviewViewModel(
                cameraUseCase = get(),
                resourceHelper = get()
            )
        }
        factory<CameraUseCase> {
            CameraXUseCase(
                application = androidApplication()
            )
        }
    }

val barcodeScannerModules = listOf(barcodeScannerModule)
