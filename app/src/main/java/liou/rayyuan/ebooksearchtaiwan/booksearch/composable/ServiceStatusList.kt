package liou.rayyuan.ebooksearchtaiwan.booksearch.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedCard
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
    val context = LocalContext.current
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (storeDetail in storeDetails) {
            OutlinedCard(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
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
                    Text("$title: $status")
                }
            }
        }
    }
}

//region Preview
@Preview(
    name = "Service Status List",
    group = "component",
    showBackground = true,
    showSystemUi = false,
    apiLevel = 34,
    device = MDPI_DEVICES
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
//endregion
