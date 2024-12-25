package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.repeatOnLifecycle
import com.rayliu.commonmain.domain.model.Book
import kotlinx.coroutines.flow.collectLatest
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.SearchBox
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.utils.navigateAndClean
import liou.rayyuan.ebooksearchtaiwan.navigation.BookResultDestinations
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookResultListScreen(
    viewModel: BookSearchViewModel,
    onSearchTextChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    onBookSearchItemClick: (Book) -> Unit = {},
    onPressSearchIcon: () -> Unit = {},
    onFocusActionFinish: () -> Unit = {},
    onFocusChange: (focusState: FocusState) -> Unit = {},
    showAppBarCameraButton: Boolean = false,
    onAppBarCameraButtonPress: () -> Unit = {},
    onAppBarSearchButtonPress: () -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.navigationEvents.collectLatest { destinations ->
                navHostController.navigateAndClean(destinations.route)
            }
        }
    }

    val searchKeywords =
        viewModel.searchKeywords
            .collectAsStateWithLifecycle()
            .value

    val focusAction =
        viewModel.focusTextInput
            .collectAsStateWithLifecycle()
            .value

    val virtualKeyboardAction =
        viewModel.showVirtualKeyboard
            .collectAsStateWithLifecycle()
            .value

    val enableCameraButtonClick =
        viewModel.enableCameraButtonClick
            .collectAsStateWithLifecycle()
            .value

    val enableSearchButtonClick =
        viewModel.enableSearchButtonClick
            .collectAsStateWithLifecycle()
            .value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    SearchBox(
                        text = searchKeywords,
                        onTextChange = onSearchTextChange,
                        onPressSearch = onPressSearchIcon,
                        focusAction = focusAction,
                        onFocusActionFinish = onFocusActionFinish,
                        onFocusChange = onFocusChange,
                        virtualKeyboardAction = virtualKeyboardAction,
                        showCameraButton = showAppBarCameraButton,
                        enableCameraButtonClick = enableCameraButtonClick,
                        enableSearchButtonClick = enableSearchButtonClick,
                        onCameraButtonPress = onAppBarCameraButtonPress,
                        onSearchButtonPress = onAppBarSearchButtonPress,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors().copy(
                        containerColor = EBookTheme.colors.colorBackground
                    )
            )
        },
        containerColor = EBookTheme.colors.colorBackground,
        modifier = modifier
    ) { paddings ->
        NavHost(
            navController = navHostController,
            startDestination = BookResultDestinations.ServiceStatus.route,
            modifier = Modifier.fillMaxSize().padding(paddings)
        ) {
            bookResultNavGraph(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize(),
                onBookSearchItemClick = onBookSearchItemClick
            )
        }
    }
}
