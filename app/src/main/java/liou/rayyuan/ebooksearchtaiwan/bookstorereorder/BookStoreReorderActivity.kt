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

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.reorder_page, menu)
//        val checkMarkerOption = menu.findItem(R.id.reorder_page_menu_action_check)
//        if (!isDarkTheme()) {
//            checkMarkerOption.icon?.colorFilter =
//                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
//                    ContextCompat.getColor(this, R.color.darker_gray_3B),
//                    BlendModeCompat.SRC_ATOP
//                )
//        }
//        this.checkMarkerOption = checkMarkerOption
//        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> {
//                finish()
//                return true
//            }
//
//            R.id.reorder_page_menu_action_check -> {
//                val result = viewModel.getStoreNames()
//                if (result != null) {
//                    eventTracker.logTopSelectedStoreName(result)
//                    viewModel.updateCurrentSort(result)
//                }
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

//    private fun showSaveSettingIcon() {
//        if (this::checkMarkerOption.isInitialized) {
//            checkMarkerOption.isVisible = true
//        }
//    }

    private fun render(viewState: BookStoreReorderViewState) {
        when (viewState) {
            BookStoreReorderViewState.BackToPreviousPage -> {
                finish()
            }
        }
    }
}
