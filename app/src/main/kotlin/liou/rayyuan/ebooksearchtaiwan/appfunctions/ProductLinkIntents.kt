package liou.rayyuan.ebooksearchtaiwan.appfunctions

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.appfunctions.model.OpenProductResult

data class ValidatedProductLink(
    val url: String,
    val title: String?
)

object ProductLinkIntents {
    private const val VIEW_REQUEST_CODE = 1001
    private const val SHARE_REQUEST_CODE = 1002
    private const val SHARE_MIME_TYPE = "text/plain"

    fun normalizeOrNull(
        url: String,
        title: String?
    ): ValidatedProductLink? {
        val normalizedUrl = ProductLinkValidator.normalize(url) ?: return null
        return ValidatedProductLink(
            url = normalizedUrl,
            title = title?.trim()?.takeIf { it.isNotEmpty() }
        )
    }

    fun create(
        context: Context,
        url: String,
        title: String?
    ): OpenProductResult? {
        val validated = normalizeOrNull(url, title) ?: return null
        val pendingIntentFlags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(validated.url))
        val sendIntent =
            Intent(Intent.ACTION_SEND).apply {
                type = SHARE_MIME_TYPE
                putExtra(
                    Intent.EXTRA_TEXT,
                    validated.title?.let { "$it \n ${validated.url}" } ?: validated.url
                )
                validated.title?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
            }
        val chooserIntent =
            Intent.createChooser(
                sendIntent,
                context.getString(R.string.menu_share_menu_appear)
            )

        return OpenProductResult(
            url = validated.url,
            title = validated.title,
            viewIntent =
                PendingIntent.getActivity(
                    context,
                    VIEW_REQUEST_CODE,
                    viewIntent,
                    pendingIntentFlags
                ),
            shareIntent =
                PendingIntent.getActivity(
                    context,
                    SHARE_REQUEST_CODE,
                    chooserIntent,
                    pendingIntentFlags
                )
        )
    }
}
