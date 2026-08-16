package liou.rayyuan.ebooksearchtaiwan.appfunctions.model

import android.app.PendingIntent
import androidx.appfunctions.AppFunctionSerializable

@AppFunctionSerializable(isDescribedByKDoc = true)
data class OpenProductResult(
    /** Normalized bookstore product URL. This is not a completed purchase. */
    val url: String,
    /** Optional book title used in the share text. */
    val title: String?,
    /** System ACTION_VIEW intent for the product URL. */
    val viewIntent: PendingIntent,
    /** Sharesheet intent. Title plus URL. This is not checkout. */
    val shareIntent: PendingIntent?
)
