package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.repeatOnLifecycle
import com.rayliu.commonmain.domain.model.Book
import com.rayliu.commonmain.domain.model.SearchRecord
import kotlinx.coroutines.flow.collectLatest
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.ConfirmRemoveRecordDialog
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.SearchBox
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.utils.navigateAndClean
import liou.rayyuan.ebooksearchtaiwan.navigation.BookResultDestinations
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookResultListScreen(
    viewModel: BookSearchViewModel,
    onSearchTextChange: (TextFieldValue) -> Unit,
    onClickCopySnapshot: () -> Unit,
    onShareResultClick: () -> Unit,
    onMenuSettingClick: () -> Unit,
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    onBookSearchItemClick: (Book) -> Unit = {},
    onPressSearchIcon: () -> Unit = {},
    onFocusActionFinish: () -> Unit = {},
    onFocusChange: (focusState: FocusState) -> Unit = {},
    showAppBarCameraButton: Boolean = false,
    onAppBarCameraButtonPress: () -> Unit = {},
    onAppBarSearchButtonPress: () -> Unit = {},
    focusOnSearchBox: () -> Unit = {},
    onSearchRecordClick: (record: SearchRecord) -> Unit = {},
    onRemoveSearchRecord: (record: SearchRecord) -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
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

    var showOptionMenu by remember { mutableStateOf(false) }
    var goingToDeleteRecords by remember { mutableStateOf<SearchRecord?>(null) }
    goingToDeleteRecords?.run {
        ConfirmRemoveRecordDialog(
            searchRecord = this,
            onDismissRequest = {
                goingToDeleteRecords = null
            },
            onRemoveSearchRecord = {
                onRemoveSearchRecord(it)
            }
        )
    }

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
                    ),
                actions = {
                    IconButton(
                        onClick = {
                            showOptionMenu = !showOptionMenu
                        },
                        colors =
                            IconButtonDefaults.iconButtonColors().copy(
                                contentColor = EBookTheme.colors.colorOnPrimary
                            )
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Option Menu")
                    }
                    DropdownMenu(
                        expanded = showOptionMenu,
                        onDismissRequest = {
                            showOptionMenu = false
                        },
                        shape = RoundedCornerShape(10.dp),
                        containerColor = EBookTheme.colors.reorderListBackgroundColor,
                        shadowElevation = 4.dp,
                        tonalElevation = 4.dp
                    ) {
                        if (viewModel.showCopyUrlOption) {
                            OptionMenuItem(
                                title = stringResource(R.string.menu_copy_snapshot),
                                onClick = {
                                    showOptionMenu = false
                                    onClickCopySnapshot()
                                }
                            )
                        }

                        if (viewModel.showShareSnapshotOption) {
                            OptionMenuItem(
                                title = stringResource(R.string.menu_share_result),
                                onClick = {
                                    showOptionMenu = false
                                    onShareResultClick()
                                }
                            )
                        }

                        OptionMenuItem(
                            title = stringResource(R.string.menu_setting),
                            onClick = {
                                showOptionMenu = false
                                onMenuSettingClick()
                            }
                        )
                    }
                }
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
                onBookSearchItemClick = onBookSearchItemClick,
                focusOnSearchBox = focusOnSearchBox,
                onSearchRecordClick = onSearchRecordClick,
                onRemoveSearchRecord = {
                    goingToDeleteRecords = it
//                    onRemoveSearchRecord(it)
                }
            )
        }
    }
}

@Composable
private fun OptionMenuItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(
        text = {
            Text(
                text = title,
                style =
                    TextStyle.Default.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
            )
        },
        onClick = onClick,
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
        colors =
            MenuDefaults.itemColors().copy(
                textColor = EBookTheme.colors.colorOnPrimary
            ),
        modifier = modifier
    )
}
