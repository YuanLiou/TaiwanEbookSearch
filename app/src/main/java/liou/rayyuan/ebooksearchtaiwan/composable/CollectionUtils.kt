package liou.rayyuan.ebooksearchtaiwan.composable

import androidx.compose.runtime.snapshots.SnapshotStateList

fun <T> Collection<T>.toMutableStateList() = SnapshotStateList<T>().also { it.addAll(this) }
