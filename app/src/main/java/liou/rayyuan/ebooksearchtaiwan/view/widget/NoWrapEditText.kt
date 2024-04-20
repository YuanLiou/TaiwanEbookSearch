package liou.rayyuan.ebooksearchtaiwan.view.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class NoWrapEditText(
    context: Context,
    attrs: AttributeSet
) : TextInputEditText(context, attrs) {
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
        if (text.isNullOrEmpty()) {
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
