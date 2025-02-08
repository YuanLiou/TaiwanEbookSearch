package liou.rayyuan.ebooksearchtaiwan.bookstorereorder

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStarted
import kotlinx.coroutines.flow.collectLatest
import liou.rayyuan.ebooksearchtaiwan.BaseActivity
import kotlinx.coroutines.launch
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class BookStoreReorderActivity : BaseActivity() {
    private val viewModel: BookStoreReorderViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EBookTheme(
                darkTheme = isDarkTheme()
            ) {
                BookStoreReorderScreen(
                    viewModel = viewModel,
                    onNavigationBack = {
                        finish()
                    },
                    onSaveSettings = {
                        val result = viewModel.getStoreNames()
                        if (result != null) {
                            eventTracker.logTopSelectedStoreName(result)
                            viewModel.updateCurrentSort(result)
                        }
                    }
                )
            }
        }

        // Render Book Result State
        lifecycleScope.launch {
            withStarted(block = {})
            viewModel.viewState.collectLatest { state ->
                if (state != null) {
                    render(state)
                }
            }
        }
        viewModel.getPreviousSavedBookResultSort()
    }

    private fun render(viewState: BookStoreReorderViewState) {
        when (viewState) {
            BookStoreReorderViewState.BackToPreviousPage -> {
                finish()
            }
        }
    }
}
