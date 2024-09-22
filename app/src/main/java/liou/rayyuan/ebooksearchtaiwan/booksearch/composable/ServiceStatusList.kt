package liou.rayyuan.ebooksearchtaiwan.booksearch.composable

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedCard
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.rayliu.commonmain.domain.model.BookStoreDetails
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.view.getLocalizedName

@Composable
fun ServiceStatusList(
    storeDetails: ImmutableList<BookStoreDetails>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxWidth()
                .nestedScroll(rememberNestedScrollInteropConnection()),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(storeDetails) {
            ServiceStatusCard(
                storeDetail = it,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun ServiceStatusCard(
    storeDetail: BookStoreDetails,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    OutlinedCard(
        shape = RoundedCornerShape(8.dp),
        border =
            BorderStroke(
                1.dp,
                color = EBookTheme.colors.colorControlNormal
            ),
        colors =
            CardDefaults.outlinedCardColors(
                containerColor = EBookTheme.colors.cardBackgroundColor,
            ),
        modifier = modifier
    ) {
        Column(
            modifier =
                Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    .fillMaxWidth()
        ) {
            val title = storeDetail.lookUpStoreName().getLocalizedName(context)
            val status =
                when (storeDetail.isEnable) {
                    true -> {
                        if (storeDetail.isOnline) {
                            stringResource(R.string.service_online)
                        } else {
                            stringResource(R.string.service_offline)
                        }
                    }

                    false -> stringResource(R.string.preference_generic_checkbox_switch_off)
                }
            Text(
                style =
                    TextStyle.Default.copy(
                        color = EBookTheme.colors.subtitle1TextColor
                    ),
                text = "$title: $status"
            )
        }
    }
}

//region Preview
@Preview(
    name = "Service Status List",
    group = "list",
    showBackground = true,
    showSystemUi = false,
    apiLevel = 34,
    device = MDPI_DEVICES
)
@Preview(
    name = "Service Status List Dark",
    group = "list",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = 34,
    device = MDPI_DEVICES,
)
@Composable
private fun ServiceStatusListPreview() {
    EBookTheme {
        ServiceStatusList(
            storeDetails =
                persistentListOf(
                    BookStoreDetails(
                        id = "booksCompany",
                        displayName = "AAA",
                        status = "",
                        url = "",
                        isOnline = true,
                        isEnable = false
                    ),
                    BookStoreDetails(
                        id = "kindle",
                        displayName = "BBB",
                        status = "",
                        url = "",
                        isOnline = true,
                        isEnable = true
                    ),
                    BookStoreDetails(
                        id = "bookWalker",
                        displayName = "CCC",
                        status = "",
                        url = "",
                        isOnline = false,
                        isEnable = true
                    ),
                )
        )
    }
}

@Preview(
    name = "Service Status Card",
    group = "component",
    showBackground = true,
    showSystemUi = false,
    apiLevel = 34,
    device = MDPI_DEVICES
)
@Preview(
    name = "Service Status Card Dark",
    group = "component",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = 34,
    device = MDPI_DEVICES,
)
@Composable
private fun ServiceStatusCardPreview() {
    EBookTheme {
        ServiceStatusCard(
            storeDetail =
                BookStoreDetails(
                    id = "booksCompany",
                    displayName = "AAA",
                    status = "",
                    url = "",
                    isOnline = true,
                    isEnable = false
                )
        )
    }
}
//endregion
