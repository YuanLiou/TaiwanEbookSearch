import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.rayliu.commonmain.di.sharedModule
import di.desktopAppModule
import org.koin.core.context.startKoin

fun main() {
    // 啟動 Koin，並載入共享模組與桌面平台專屬模組
    startKoin {
        modules(sharedModule, desktopAppModule)
    }

    // 啟動 Compose for Desktop 應用程式
    application {
        Window(onCloseRequest = ::exitApplication, title = "Taiwan Ebook Search") {
            // 在這裡放置您的 Compose UI 程式碼。
            // 您將能夠在這裡使用 koinInject() 來獲取 ViewModel。
            // e.g., val viewModel: MyViewModel = koinInject()
            // AppTheme {
            //     MainScreen(viewModel)
            // }
        }
    }
}
