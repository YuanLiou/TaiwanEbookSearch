package liou.rayyuan.ebooksearchtaiwan.view.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class NoWrapEditText(context: Context, attrs: AttributeSet): AppCompatEditText(context, attrs) {

    private val pattern by lazy {
        "\\n+".toRegex()
    }

    override fun onTextContextMenuItem(id: Int): Boolean {
        val performAction = super.onTextContextMenuItem(id)
        if (id == android.R.id.paste) {
            cleanAllWrapSigns()
        }

        return performAction
    }

    private fun cleanAllWrapSigns() {
        if (TextUtils.isEmpty(text)) {
            return
        }

        var currentText = text.toString()
        System.getProperty("line.separator")?.run {
            currentText = currentText.replace(this, "")
        }
        currentText = currentText.replace(pattern, "")
        setText(currentText.trim())
        setSelection(text?.length ?: 0)
    }

}