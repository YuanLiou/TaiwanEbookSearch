package liou.rayyuan.ebooksearchtaiwan.model.entity

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookHeader(@StringRes val stringId: Int, val isEmptyResult: Boolean) : Parcelable, AdapterItem
