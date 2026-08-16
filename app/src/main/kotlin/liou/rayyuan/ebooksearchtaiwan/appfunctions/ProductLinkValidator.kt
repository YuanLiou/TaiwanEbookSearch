package liou.rayyuan.ebooksearchtaiwan.appfunctions

import java.net.URI

object ProductLinkValidator {
    fun normalize(url: String): String? {
        val normalizedUrl = url.trim()
        if (normalizedUrl.isBlank()) {
            return null
        }

        val uri = runCatching { URI(normalizedUrl) }.getOrNull() ?: return null
        val scheme = uri.scheme ?: return null
        if (!scheme.equals("http", ignoreCase = true) &&
            !scheme.equals("https", ignoreCase = true)
        ) {
            return null
        }
        if (uri.host.isNullOrBlank()) {
            return null
        }

        return normalizedUrl
    }
}
