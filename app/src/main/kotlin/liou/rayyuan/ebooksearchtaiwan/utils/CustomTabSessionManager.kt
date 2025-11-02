package liou.rayyuan.ebooksearchtaiwan.utils

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.domain.usecase.GetDefaultBookSortUseCase
import kotlinx.coroutines.flow.firstOrNull
import liou.rayyuan.ebooksearchtaiwan.R

class CustomTabSessionManager(
    private val getDefaultBookSortUseCase: GetDefaultBookSortUseCase
) {
    private var customTabsClient: CustomTabsClient? = null
    var customTabsSession: CustomTabsSession? = null
        private set
    private var topSelectedBookStoreUrl: String? = null

    private val connections =
        object : CustomTabsServiceConnection() {
            override fun onServiceDisconnected(name: ComponentName?) {
                customTabsClient = null
                customTabsSession = null
            }

            override fun onCustomTabsServiceConnected(
                name: ComponentName,
                client: CustomTabsClient
            ) {
                client.warmup(0)
                customTabsSession = client.newSession(CustomTabsCallback())
                customTabsClient = client
                topSelectedBookStoreUrl?.let { url ->
                    customTabsSession?.mayLaunchUrl(Uri.parse(url), null, null)
                }
            }
        }

    suspend fun bindCustomTabService(context: Context): Boolean {
        if (customTabsClient != null) {
            return true
        }

        val topSelectedStoreUrl = findTopSelectedStoreUrl(context)
        this.topSelectedBookStoreUrl = topSelectedStoreUrl

        val packageName = CustomTabsClient.getPackageName(context, null) ?: return false
        CustomTabsClient.bindCustomTabsService(context, packageName, connections)
        return true
    }

    private suspend fun findTopSelectedStoreUrl(context: Context): String? {
        val bookSorts = getDefaultBookSortUseCase().firstOrNull().orEmpty()
        if (bookSorts.isEmpty()) {
            return null
        }

        return when (bookSorts.first()) {
            DefaultStoreNames.BOOK_COMPANY -> context.getString(R.string.url_book_company)
            DefaultStoreNames.KINDLE -> context.getString(R.string.url_amazon)
            DefaultStoreNames.READMOO -> context.getString(R.string.url_readmoo)
            DefaultStoreNames.KOBO -> context.getString(R.string.url_kobo)
            DefaultStoreNames.TAAZE -> context.getString(R.string.url_taaze)
            DefaultStoreNames.BOOK_WALKER -> context.getString(R.string.url_bookwalker)
            DefaultStoreNames.PLAY_STORE -> context.getString(R.string.url_playstore)
            DefaultStoreNames.PUBU -> context.getString(R.string.url_pubu)
            DefaultStoreNames.HYREAD -> context.getString(R.string.url_hyread)
            else -> null
        }
    }
}
