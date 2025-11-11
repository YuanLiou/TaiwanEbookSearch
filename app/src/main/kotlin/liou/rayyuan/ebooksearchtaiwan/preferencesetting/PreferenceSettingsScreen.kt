package liou.rayyuan.ebooksearchtaiwan.preferencesetting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.alorma.compose.settings.ui.base.internal.SettingsTileDefaults
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.ui.theme.blue_green_you
import liou.rayyuan.ebooksearchtaiwan.ui.theme.light_blue_green_you
import liou.rayyuan.ebooksearchtaiwan.ui.theme.pure_white

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceSettingsScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {}
) {
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
    ) {
        PreferenceSettingsScreenContent(
            modifier = Modifier.padding(it)
        )
    }
}

@Composable
private fun PreferenceSettingsScreenContent(modifier: Modifier = Modifier) {
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
                    Text(stringResource(R.string.preference_generic_checkbox_switch_off))
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
                state = true,
                onCheckedChange = {}
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
                onClick = {}
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
                    Text(stringResource(R.string.preference_sort_by_book_price_on_summary))
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
                state = true,
                onCheckedChange = {}
            )
            SettingsSwitch(
                title = {
                    Text(stringResource(R.string.preference_custom_tab_description))
                },
                subtitle = {
                    Text(stringResource(R.string.preference_custom_tab_on_summary))
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
                state = true,
                onCheckedChange = {}
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
                onClick = {}
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
                onClick = {}
            )
        }
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
                modifier = Modifier.padding(it)
            )
        }
    }
}
