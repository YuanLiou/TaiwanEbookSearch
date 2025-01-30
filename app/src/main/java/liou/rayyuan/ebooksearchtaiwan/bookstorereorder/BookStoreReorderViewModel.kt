package liou.rayyuan.ebooksearchtaiwan.bookstorereorder

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.domain.usecase.GetDefaultBookSortUseCase
import com.rayliu.commonmain.domain.usecase.SaveDefaultBookSortUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import liou.rayyuan.ebooksearchtaiwan.arch.IModel

class BookStoreReorderViewModel(
    private val getDefaultBookSortUseCase: GetDefaultBookSortUseCase,
    private val saveDefaultBookBookSortUseCase: SaveDefaultBookSortUseCase
) : ViewModel(),
    IModel<BookStoreReorderViewState, BookStoreReorderUserIntent> {
    override val userIntents: MutableSharedFlow<BookStoreReorderUserIntent> = MutableSharedFlow()
    private val _bookStoreReorderViewState = MutableLiveData<BookStoreReorderViewState>()
    override val viewState: LiveData<BookStoreReorderViewState>
        get() = _bookStoreReorderViewState
    private val _sortedStores =
        MutableStateFlow<ImmutableList<SortedStore>>(
            persistentListOf()
        )
    val sortedStores
        get() = _sortedStores.asStateFlow()

    init {
        setupUserIntentHanding()
    }

    private fun setupUserIntentHanding() {
        viewModelScope.launch {
            userIntents.collect {
                when (it) {
                    BookStoreReorderUserIntent.GetPreviousSavedSort -> {
                        getPreviousSavedBookResultSort()
                    }

                    is BookStoreReorderUserIntent.UpdateSort -> {
                        updateCurrentSort(it.bookSorts)
                    }
                }
            }
        }
    }

    private fun getPreviousSavedBookResultSort() {
        viewModelScope.launch {
            val defaultSort = getDefaultBookSortUseCase().first()
            updateScreen(BookStoreReorderViewState.PrepareBookSort(defaultSort))
            _sortedStores.value = convertToSortedStore(defaultSort).toImmutableList()
        }
    }

    private fun updateCurrentSort(bookSorts: List<DefaultStoreNames>) {
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
        val disableStores = DefaultStoreNames.values().toMutableList()
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
}
