package liou.rayyuan.ebooksearchtaiwan.preferencesetting.widget

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.preference.ListPreferenceDialogFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MaterialListPreference : ListPreferenceDialogFragmentCompat() {
    private var clickedButton = 0
    private var isDialogCloseByDismiss = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = activity ?: return super.onCreateDialog(savedInstanceState)
        clickedButton = DialogInterface.BUTTON_NEGATIVE
        val builder =
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(preference.dialogTitle)
                .setIcon(preference.dialogIcon)
                .setPositiveButton(preference.positiveButtonText, this)
                .setNegativeButton(preference.negativeButtonText, this)

        val contentView = onCreateDialogView(context)
        if (contentView != null) {
            onBindDialogView(contentView)
            builder.setView(contentView)
        } else {
            builder.setMessage(preference.dialogMessage)
        }
        onPrepareDialogBuilder(builder)
        return builder.create()
    }

    override fun onClick(
        dialog: DialogInterface,
        which: Int
    ) {
        clickedButton = which
    }

    override fun onDismiss(dialog: DialogInterface) {
        isDialogCloseByDismiss = true
        super.onDismiss(dialog)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (isDialogCloseByDismiss) {
            isDialogCloseByDismiss = false
            super.onDialogClosed(clickedButton == DialogInterface.BUTTON_POSITIVE)
        } else {
            super.onDialogClosed(positiveResult)
        }
    }
}
