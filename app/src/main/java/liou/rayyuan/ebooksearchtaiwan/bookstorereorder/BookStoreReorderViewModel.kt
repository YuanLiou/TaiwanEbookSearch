package liou.rayyuan.ebooksearchtaiwan.bookstorereorder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.domain.usecase.GetDefaultBookSortUseCase
import com.rayliu.commonmain.domain.usecase.SaveDefaultBookSortUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import liou.rayyuan.ebooksearchtaiwan.arch.IModel

class BookStoreReorderViewModel(
    private val getDefaultBookSortUseCase: GetDefaultBookSortUseCase,
    private val saveDefaultBookBookSortUseCase: SaveDefaultBookSortUseCase
) : ViewModel(),
    IModel<BookStoreReorderViewState, BookStoreReorderUserIntent> {
    override val userIntents: Channel<BookStoreReorderUserIntent> = Channel(Channel.UNLIMITED)
    private val _bookStoreReorderViewState = MutableLiveData<BookStoreReorderViewState>()
    override val viewState: LiveData<BookStoreReorderViewState>
        get() = _bookStoreReorderViewState

    init {
        setupUserIntentHanding()
    }

    private fun setupUserIntentHanding() {
        viewModelScope.launch {
            userIntents.consumeAsFlow().collect {
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
}
