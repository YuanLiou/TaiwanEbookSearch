package liou.rayyuan.ebooksearchtaiwan.preferencesetting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.alorma.compose.settings.ui.base.internal.SettingsTileDefaults
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.ui.theme.blue_green_you
import liou.rayyuan.ebooksearchtaiwan.ui.theme.pure_white
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceSettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: PreferenceSettingsViewModel = koinViewModel(),
    onBackPressed: () -> Unit = {},
    onRecreateRequest: () -> Unit = {},
    onClickReorderBookStore: () -> Unit = {}
) {
    val isFollowSystemTheme by viewModel.isFollowSystemTheme.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val isPreferCustomTab by viewModel.isPreferCustomTab.collectAsStateWithLifecycle()
    val isSearchResultSortByPrice by viewModel.isSearchResultSortByPrice.collectAsStateWithLifecycle()

    var showDeleteRecordSuccessDialog by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.showClearHistorySuccessDialog
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { show ->
                showDeleteRecordSuccessDialog = show
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.menu_setting))
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.menu_setting)
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { scaffoldPadding ->
        PreferenceSettingsScreenContent(
            isFollowSystemTheme = isFollowSystemTheme,
            isDarkTheme = isDarkTheme,
            isPreferCustomTab = isPreferCustomTab,
            isSearchResultSortByPrice = isSearchResultSortByPrice,
            showDeleteRecordSuccessDialog = showDeleteRecordSuccessDialog,
            onIsFollowSystemThemeChange = {
                viewModel.onIsFollowSystemThemeChange(it)
                onRecreateRequest()
            },
            onIsDarkThemeChange = {
                viewModel.onIsDarkThemeChange(it)
                onRecreateRequest()
            },
            onSearchResultSortByPriceChange = { viewModel.onSearchResultSortByPriceChange(it) },
            onIsPreferCustomTabChange = { viewModel.onIsPreferCustomTabChange(it) },
            onClickReorderBookStore = onClickReorderBookStore,
            onClearAllRecords = { viewModel.deleteAllSearchRecords() },
            onDismissDeleteRecordSuccessDialog = { showDeleteRecordSuccessDialog = false },
            modifier = Modifier.padding(scaffoldPadding)
        )
    }
}

@Composable
private fun PreferenceSettingsScreenContent(
    isFollowSystemTheme: Boolean,
    isDarkTheme: Boolean,
    isPreferCustomTab: Boolean,
    isSearchResultSortByPrice: Boolean,
    modifier: Modifier = Modifier,
    showDeleteRecordSuccessDialog: Boolean = false,
    onIsFollowSystemThemeChange: (Boolean) -> Unit = {},
    onIsDarkThemeChange: (Boolean) -> Unit = {},
    onSearchResultSortByPriceChange: (Boolean) -> Unit = {},
    onIsPreferCustomTabChange: (Boolean) -> Unit = {},
    onClickReorderBookStore: () -> Unit = {},
    onClearAllRecords: () -> Unit = {},
    onDismissDeleteRecordSuccessDialog: () -> Unit = {}
) {
    var showThemeDialog by remember { mutableStateOf(false) }
    if (showThemeDialog) {
        ThemeSettingDialog(
            isDarkTheme = isDarkTheme,
            onDismissRequest = { showThemeDialog = false },
            onThemeChange = {
                onIsDarkThemeChange(it)
                showThemeDialog = false
            }
        )
    }

    var showClearHistoryDialog by remember { mutableStateOf(false) }

    if (showClearHistoryDialog) {
        ClearSearchHistoryDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            onConfirm = {
                onClearAllRecords()
                showClearHistoryDialog = false
            }
        )
    }

    if (showDeleteRecordSuccessDialog) {
        ClearSearchHistorySuccessDialog(
            onDismissRequest = onDismissDeleteRecordSuccessDialog
        )
    }

    Column(
        modifier =
            modifier
                .background(EBookTheme.colors.cardBackgroundColor)
                .fillMaxSize()
    ) {
        SettingsGroup(
            title = {
                Text(stringResource(R.string.preference_category_appearance))
            },
            modifier = Modifier
        ) {
            SettingsSwitch(
                title = {
                    Text(stringResource(R.string.preference_follow_system_theme))
                },
                subtitle = {
                    if (isFollowSystemTheme) {
                        Text(stringResource(R.string.preference_follow_system_switch_on))
                    } else {
                        Text(stringResource(R.string.preference_generic_checkbox_switch_off))
                    }
                },
                colors =
                    SettingsTileDefaults.colors(
                        containerColor = EBookTheme.colors.cardBackgroundColor,
                        titleColor = EBookTheme.colors.headline6TextColor
                    ),
                switchColors =
                    SwitchDefaults.colors(
                        checkedThumbColor = pure_white,
                        checkedTrackColor = blue_green_you
                    ),
                state = isFollowSystemTheme,
                onCheckedChange = onIsFollowSystemThemeChange
            )
            SettingsMenuLink(
                title = {
                    Text(stringResource(R.string.preference_appearance_theme_title))
                },
                subtitle = {
                    Text(stringResource(R.string.preference_appearance_theme_description))
                },
                colors =
                    SettingsTileDefaults.colors(
                        containerColor = EBookTheme.colors.cardBackgroundColor,
                        titleColor = EBookTheme.colors.headline6TextColor
                    ),
                enabled = !isFollowSystemTheme,
                onClick = {
                    showThemeDialog = true
                }
            )
        }
        SettingsGroup(
            title = {
                Text(stringResource(R.string.preference_category_behaviours))
            }
        ) {
            SettingsSwitch(
                title = {
                    Text(stringResource(R.string.preference_sort_by_book_price))
                },
                subtitle = {
                    if (isSearchResultSortByPrice) {
                        Text(stringResource(R.string.preference_sort_by_book_price_on_summary))
                    } else {
                        Text(stringResource(R.string.preference_sort_by_book_price_off_summary))
                    }
                },
                colors =
                    SettingsTileDefaults.colors(
                        containerColor = EBookTheme.colors.cardBackgroundColor,
                        titleColor = EBookTheme.colors.headline6TextColor
                    ),
                switchColors =
                    SwitchDefaults.colors(
                        checkedThumbColor = pure_white,
                        checkedTrackColor = blue_green_you
                    ),
                state = isSearchResultSortByPrice,
                onCheckedChange = onSearchResultSortByPriceChange
            )
            SettingsSwitch(
                title = {
                    Text(stringResource(R.string.preference_custom_tab_description))
                },
                subtitle = {
                    if (isPreferCustomTab) {
                        Text(stringResource(R.string.preference_custom_tab_on_summary))
                    } else {
                        Text(stringResource(R.string.preference_custom_tab_off_summary))
                    }
                },
                colors =
                    SettingsTileDefaults.colors(
                        containerColor = EBookTheme.colors.cardBackgroundColor,
                        titleColor = EBookTheme.colors.headline6TextColor
                    ),
                switchColors =
                    SwitchDefaults.colors(
                        checkedThumbColor = pure_white,
                        checkedTrackColor = blue_green_you
                    ),
                state = isPreferCustomTab,
                onCheckedChange = onIsPreferCustomTabChange
            )
            SettingsMenuLink(
                title = {
                    Text(stringResource(R.string.activty_reorder_title))
                },
                subtitle = {
                    Text(stringResource(R.string.summary_reorder_bookstore))
                },
                colors =
                    SettingsTileDefaults.colors(
                        containerColor = EBookTheme.colors.cardBackgroundColor,
                        titleColor = EBookTheme.colors.headline6TextColor
                    ),
                onClick = onClickReorderBookStore
            )
            SettingsMenuLink(
                title = {
                    Text(stringResource(R.string.preference_clean_all_records))
                },
                colors =
                    SettingsTileDefaults.colors(
                        containerColor = EBookTheme.colors.cardBackgroundColor,
                        titleColor = EBookTheme.colors.headline6TextColor
                    ),
                onClick = { showClearHistoryDialog = true }
            )
        }
    }
}

@Composable
fun ThemeSettingDialog(
    isDarkTheme: Boolean,
    onDismissRequest: () -> Unit,
    onThemeChange: (isDark: Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(id = R.string.preference_appearance_theme_title))
        },
        text = {
            ThemeSettingDialogContent(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = stringResource(id = android.R.string.cancel),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun ThemeSettingDialogContent(
    isDarkTheme: Boolean,
    onThemeChange: (isDark: Boolean) -> Unit
) {
    val themeOptions = stringArrayResource(id = R.array.theme_options)
    val themeOptionsValues = stringArrayResource(id = R.array.theme_options_values)

    Column {
        themeOptions.forEachIndexed { index, themeName ->
            val themeValue = themeOptionsValues[index]
            val isDark = themeValue == "dark"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { onThemeChange(isDark) }
                        .padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = isDarkTheme == isDark,
                    onClick = { onThemeChange(isDark) }
                )
                Text(
                    text = themeName,
                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp
                        ),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ClearSearchHistoryDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(id = R.string.preference_clean_all_records))
        },
        text = {
            Text(text = stringResource(id = R.string.dialog_clean_all_records))
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = stringResource(id = R.string.dialog_cancel),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(id = R.string.dialog_ok),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    )
}

@Composable
fun ClearSearchHistorySuccessDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(id = R.string.preference_clean_all_records))
        },
        text = {
            Text(text = stringResource(id = R.string.dialog_clean_all_records_cleaned))
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = stringResource(id = R.string.dialog_ok),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    )
}

@Preview(
    locale = "zh-rTW",
    showBackground = true,
    group = "dialog"
)
@Composable
private fun ClearSearchHistoryDialogPreview() {
    EBookTheme {
        ClearSearchHistoryDialog(
            onDismissRequest = {},
            onConfirm = {}
        )
    }
}

@Preview(
    locale = "zh-rTW",
    showBackground = true,
    group = "dialog"
)
@Composable
private fun ClearSearchHistorySuccessDialogPreview() {
    EBookTheme {
        ClearSearchHistorySuccessDialog(
            onDismissRequest = {}
        )
    }
}

@Preview(
    locale = "zh-rTW",
    showBackground = true,
    group = "dialog"
)
@Composable
private fun ThemeSettingDialogPreview() {
    EBookTheme {
        var isDarkTheme by remember { mutableStateOf(false) }
        ThemeSettingDialog(
            isDarkTheme = isDarkTheme,
            onDismissRequest = {},
            onThemeChange = { isDarkTheme = it }
        )
    }
}

@Preview(
    locale = "zh-rTW",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun PreferenceSettingsScreenPreview() {
    EBookTheme {
        Scaffold {
            PreferenceSettingsScreenContent(
                isFollowSystemTheme = true,
                isDarkTheme = true,
                isPreferCustomTab = true,
                isSearchResultSortByPrice = true,
                modifier = Modifier.padding(it)
            )
        }
    }
}
