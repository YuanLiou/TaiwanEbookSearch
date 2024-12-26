package liou.rayyuan.ebooksearchtaiwan.booksearch.composable

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rayliu.commonmain.domain.model.SearchRecord
import liou.rayyuan.ebooksearchtaiwan.composable.debounceClick
import liou.rayyuan.ebooksearchtaiwan.composable.iconpack.BaselineClear24Px
import liou.rayyuan.ebooksearchtaiwan.composable.iconpack.EBookIcons
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.ui.theme.dark_gray

@Composable
fun SearchRecords(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    ElevatedCard(
        elevation =
            CardDefaults.elevatedCardElevation(
                defaultElevation = 8.dp,
            ),
        shape = RoundedCornerShape(10.dp),
        modifier =
            modifier
                .padding(16.dp)
                .fillMaxWidth()
    ) {
        content()
    }
}

@Composable
fun SearchRecordItem(
    searchRecord: SearchRecord,
    modifier: Modifier = Modifier,
    onRecordClick: (record: SearchRecord) -> Unit = {},
    onRemoveRecordClick: (record: SearchRecord) -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable {
                    onRecordClick(searchRecord)
                }
                .padding(horizontal = 16.dp)
    ) {
        Text(
            text = searchRecord.text,
            style =
                TextStyle.Default.copy(
                    fontSize = 16.sp
                ),
            modifier = Modifier.weight(1f)
        )
        Image(
            imageVector = EBookIcons.BaselineClear24Px,
            contentDescription = "Clear Search Record",
            colorFilter = ColorFilter.tint(dark_gray),
            modifier =
                Modifier.debounceClick(
                    debounceInterval = CLICK_MILLISECOND_THRESHOLD,
                    enabled = true,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false)
                ) {
                    onRemoveRecordClick(searchRecord)
                }
        )
    }
}

//region Preview
@Preview(
    name = "Search Records",
    group = "component",
    showBackground = true,
    showSystemUi = false,
    device = MDPI_DEVICES
)
@Preview(
    name = "Search Records Dark",
    group = "component",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = MDPI_DEVICES
)
@Composable
private fun SearchBoxPreview() {
    EBookTheme {
        SearchRecords {
            SearchRecordItem(
                searchRecord =
                    SearchRecord(
                        id = 1,
                        text = "Search Record 1",
                        times = 1
                    )
            )
            SearchRecordItem(
                searchRecord =
                    SearchRecord(
                        id = 2,
                        text = "Search Record 2",
                        times = 2
                    )
            )
            SearchRecordItem(
                searchRecord =
                    SearchRecord(
                        id = 3,
                        text = "Search Record 3",
                        times = 3
                    )
            )
        }
    }
}
//endregion
