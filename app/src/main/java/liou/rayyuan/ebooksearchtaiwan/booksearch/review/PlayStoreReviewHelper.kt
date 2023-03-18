package liou.rayyuan.ebooksearchtaiwan.booksearch.review

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import java.lang.IllegalStateException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PlayStoreReviewHelper(context: Context) {

    private val reviewManager by lazy {
        ReviewManagerFactory.create(context)
    }

    suspend fun prepareReviewInfo() = suspendCoroutine<ReviewInfo> { continuation ->
        val task = reviewManager.requestReviewFlow()
        task.addOnCompleteListener {
            if (it.isSuccessful) {
                continuation.resume(it.result)
            } else {
                val throwable = it.exception ?: IllegalStateException("Prepare Review Manager Failed")
                continuation.resumeWithException(throwable)
            }
        }
    }

    suspend fun showReviewDialog(activity: Activity, reviewInfo: ReviewInfo) = suspendCoroutine { continuation ->
        val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
        flow.addOnCompleteListener {
            continuation.resume(Unit)
        }
    }
}