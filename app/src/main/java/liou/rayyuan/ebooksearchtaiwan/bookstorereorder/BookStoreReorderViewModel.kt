package liou.rayyuan.ebooksearchtaiwan.bookstorereorder

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.domain.usecase.GetDefaultBookSortUseCase
import com.rayliu.commonmain.domain.usecase.SaveDefaultBookSortUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import liou.rayyuan.ebooksearchtaiwan.bookstorereorder.model.SortedStore
import liou.rayyuan.ebooksearchtaiwan.utils.DeviceVibrateHelper

class BookStoreReorderViewModel(
    private val getDefaultBookSortUseCase: GetDefaultBookSortUseCase,
    private val saveDefaultBookBookSortUseCase: SaveDefaultBookSortUseCase,
    private val deviceVibrateHelper: DeviceVibrateHelper
) : ViewModel() {
    private val _bookStoreReorderViewState = MutableStateFlow<BookStoreReorderViewState?>(null)
    val viewState get() = _bookStoreReorderViewState.asStateFlow()
    private val _sortedStores =
        MutableStateFlow<ImmutableList<SortedStore>>(persistentListOf())
    val sortedStores
        get() = _sortedStores.asStateFlow()

    var currentBookStoreSort: SnapshotStateList<SortedStore>? = null

    fun getPreviousSavedBookResultSort() {
        viewModelScope.launch {
            val defaultSort = getDefaultBookSortUseCase().first()
            _sortedStores.value = convertToSortedStore(defaultSort).toImmutableList()
        }
    }

    fun updateCurrentSort(bookSorts: List<DefaultStoreNames>) {
        viewModelScope.launch {
            saveDefaultBookBookSortUseCase(bookSorts)
            updateScreen(BookStoreReorderViewState.BackToPreviousPage)
        }
    }

    private fun updateScreen(viewState: BookStoreReorderViewState) {
        _bookStoreReorderViewState.value = viewState
    }

    private fun convertToSortedStore(displayStores: List<DefaultStoreNames>): List<SortedStore> {
        Log.i("BookstoreNameAdapter", "display stores = $displayStores")
        val disableStores = DefaultStoreNames.entries.toMutableList()
        disableStores.remove(DefaultStoreNames.BEST_RESULT)
        disableStores.remove(DefaultStoreNames.UNKNOWN)
        disableStores.removeAll(displayStores)

        val results = mutableListOf<SortedStore>()
        if (disableStores.isNotEmpty()) {
            Log.i("BookstoreNameAdapter", "disabled bookstore are = $disableStores")
            val disabledList =
                disableStores.map {
                    SortedStore(it, false)
                }

            val bookStores =
                displayStores.map {
                    SortedStore(it, true)
                }.toMutableList()
            bookStores.addAll(disabledList)
            results.addAll(bookStores)
        } else {
            val bookStores =
                displayStores.map {
                    SortedStore(it, true)
                }
            results.addAll(bookStores)
        }
        return results.toList()
    }

    fun getStoreNames(): List<DefaultStoreNames>? =
        currentBookStoreSort?.filter {
            it.isEnable.value
        }?.map {
            it.defaultStoreName
        }

    fun vibrateDevice() {
        deviceVibrateHelper.vibrate(50L)
    }
}
