package liou.rayyuan.ebooksearchtaiwan.booksearch

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.rayliu.commonmain.domain.model.Book
import com.rayliu.commonmain.domain.model.SearchRecord
import liou.rayyuan.ebooksearchtaiwan.BookResultDestinations
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.ConfirmRemoveRecordDialog
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.SearchBox
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.SearchRecordItem
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.SearchRecords
import liou.rayyuan.ebooksearchtaiwan.composable.EBookDropdownMenu
import liou.rayyuan.ebooksearchtaiwan.composable.OptionMenuItem
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookResultListScreen(
    viewModel: BookSearchViewModel,
    onMenuSettingClick: () -> Unit,
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    onBookSearchItemClick: (Book) -> Unit = {},
    showAppBarCameraButton: Boolean = false,
    onAppBarCameraButtonPress: () -> Unit = {}
) {
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

    val isLoadingResult =
        viewModel.isLoadingResult
            .collectAsStateWithLifecycle()
            .value

    val showSearchRecords =
        viewModel.isShowSearchRecord
            .collectAsStateWithLifecycle()
            .value
    val searchRecords = viewModel.searchRecords.collectAsLazyPagingItems()

    var showOptionMenu by remember { mutableStateOf(false) }
    var goingToDeleteRecords by remember { mutableStateOf<SearchRecord?>(null) }
    goingToDeleteRecords?.run {
        ConfirmRemoveRecordDialog(
            searchRecord = this,
            onDismissRequest = {
                goingToDeleteRecords = null
            },
            onDeleteSearchRecord = {
                viewModel.deleteRecords(it)
            }
        )
    }

    val appBarState = rememberTopAppBarState()
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(
            appBarState,
            canScroll = { !showSearchRecords }
        )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    SearchBox(
                        text = searchKeywords,
                        onTextChange = {
                            viewModel.updateKeyword(it)
                        },
                        onPressSearch = {
                            viewModel.searchBook()
                        },
                        focusAction = focusAction,
                        onFocusActionFinish = {
                            viewModel.resetFocusAction()
                        },
                        onFocusChange = {
                            viewModel.updateTextInputFocusState(it.isFocused)
                            viewModel.focusOnEditText(it.isFocused)
                        },
                        virtualKeyboardAction = virtualKeyboardAction,
                        showCameraButton = showAppBarCameraButton,
                        enableTextField = !isLoadingResult,
                        enableCameraButtonClick = enableCameraButtonClick,
                        enableSearchButtonClick = enableSearchButtonClick,
                        onCameraButtonPress = onAppBarCameraButtonPress,
                        onSearchButtonPress = {
                            viewModel.forceShowOrHideVirtualKeyboard(false)
                            viewModel.forceFocusOrUnfocusKeywordTextInput(false)
                            viewModel.searchBook()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors().copy(
                        containerColor = EBookTheme.colors.colorBackground,
                        scrolledContainerColor = EBookTheme.colors.colorBackground
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
                    EBookDropdownMenu(
                        showOptionMenu = showOptionMenu,
                        onDismissRequest = {
                            showOptionMenu = false
                        }
                    ) {
                        if (viewModel.showCopyUrlOption) {
                            OptionMenuItem(
                                title = stringResource(R.string.menu_copy_snapshot),
                                onClick = {
                                    showOptionMenu = false
                                    viewModel.copySnapshotToClipboard()
                                }
                            )
                        }

                        if (viewModel.showShareSnapshotOption) {
                            OptionMenuItem(
                                title = stringResource(R.string.menu_share_result),
                                onClick = {
                                    showOptionMenu = false
                                    viewModel.shareCurrentSnapshot()
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
                },
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = EBookTheme.colors.colorBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddings ->
        Box(
            modifier =
                Modifier.fillMaxSize()
        ) {
            AnimatedVisibility(
                visible = showSearchRecords,
                enter = expandVertically(),
                exit = shrinkVertically(),
                modifier = Modifier.zIndex(3f)
            ) {
                SearchRecords(
                    itemCounts = searchRecords.itemCount,
                    modifier = Modifier.padding(top = paddings.calculateTopPadding())
                ) {
                    LazyColumn {
                        items(count = searchRecords.itemCount, key = searchRecords.itemKey { it.id ?: -1 }) { index ->
                            searchRecords[index]?.let { searchRecord ->
                                if (index != 0) {
                                    HorizontalDivider(
                                        color = EBookTheme.colors.colorControlNormal.copy(alpha = 0.3f),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }

                                SearchRecordItem(
                                    searchRecord = searchRecord,
                                    onRecordClick = {
                                        val keyword = searchRecord.text
                                        viewModel.updateKeyword(TextFieldValue(keyword, selection = TextRange(keyword.length)))
                                        viewModel.forceFocusOrUnfocusKeywordTextInput(false)
                                        viewModel.forceShowOrHideVirtualKeyboard(false)
                                        viewModel.searchBook(keyword)
                                    },
                                    onRemoveRecordClick = {
                                        goingToDeleteRecords = it
                                    }
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showSearchRecords,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.zIndex(2f)
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable(
                                enabled = true,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                viewModel.showSearchRecords(false)
                                viewModel.forceShowOrHideVirtualKeyboard(false)
                            }
                )
            }

            NavHost(
                navController = navHostController,
                startDestination = BookResultDestinations.ServiceStatus.route,
                modifier =
                    Modifier
                        .fillMaxSize()
            ) {
                bookResultNavGraph(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize().consumeWindowInsets(paddings),
                    contentPaddings = paddings,
                    onBookSearchItemClick = onBookSearchItemClick,
                    focusOnSearchBox = {
                        viewModel.forceFocusOrUnfocusKeywordTextInput(true)
                        viewModel.forceShowOrHideVirtualKeyboard(true)
                    },
                    onListScroll = {
                        if (viewModel.isTextInputFocused.value) {
                            viewModel.forceFocusOrUnfocusKeywordTextInput(false)
                        }
                    }
                )
            }
        }
    }
}
