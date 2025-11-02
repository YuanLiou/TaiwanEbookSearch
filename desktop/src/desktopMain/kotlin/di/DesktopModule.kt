package di

import com.rayliu.commonmain.data.DatabaseDriverFactory
import org.koin.dsl.module

/**
 * Desktop 平台專屬的 Koin 模組。
 * 提供桌面版的 DatabaseDriverFactory。
 */
val desktopAppModule = module {
    single {
        DatabaseDriverFactory()
    }
}
