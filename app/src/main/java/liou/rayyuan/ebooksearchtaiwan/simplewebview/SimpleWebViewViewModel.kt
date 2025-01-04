package liou.rayyuan.ebooksearchtaiwan.simplewebview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rayliu.commonmain.domain.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SimpleWebViewViewModel(
    handle: SavedStateHandle
) : ViewModel() {
    private val _book = MutableStateFlow<Book?>(null)
    val book = _book.asStateFlow()

    private val _showCloseButton = MutableStateFlow(false)
    val showCloseButton = _showCloseButton.asStateFlow()

    init {
        val currentBook: Book? = handle.get<Book>(SimpleWebViewFragment.KEY_BOOK)
        _book.value = currentBook

        val shouldShowCloseButton: Boolean = handle.get<Boolean>(SimpleWebViewFragment.KEY_SHOW_CLOSE_BUTTON) ?: false
        _showCloseButton.value = shouldShowCloseButton
    }
}
