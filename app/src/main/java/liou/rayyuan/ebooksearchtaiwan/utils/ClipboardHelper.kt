package liou.rayyuan.ebooksearchtaiwan.utils

import android.content.ClipData
import android.content.ClipboardManager

class ClipboardHelper(
    private val clipboardManager: ClipboardManager
) {
    fun addToClipboard(text: String) {
        val clipData = ClipData.newPlainText("snapshot_url", text)
        clipboardManager.setPrimaryClip(clipData)
    }
}
