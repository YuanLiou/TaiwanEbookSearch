package liou.rayyuan.ebooksearchtaiwan.booksearch.composable

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rayliu.commonmain.domain.model.SearchRecord
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmRemoveRecordDialog(
    searchRecord: SearchRecord,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onRemoveSearchRecord: (record: SearchRecord) -> Unit = {}
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        ElevatedCard(
            elevation =
                CardDefaults.elevatedCardElevation(
                    defaultElevation = 10.dp
                ),
            colors =
                CardDefaults.elevatedCardColors(
                    containerColor = EBookTheme.colors.cardBackgroundColor
                ),
            modifier = Modifier
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.alert_dialog_delete_search_records),
                    style =
                        TextStyle.Default.copy(
                            color = EBookTheme.colors.headline6TextColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text =
                        stringResource(
                            R.string.alert_dialog_delete_search_record_message,
                            searchRecord.text
                        ),
                    style =
                        TextStyle.Default.copy(
                            color = EBookTheme.colors.headline6TextColor,
                            fontSize = 16.sp
                        ),
                    modifier =
                        Modifier
                            .align(Alignment.Start)
                            .padding(top = 16.dp)
                )
                Spacer(modifier = Modifier.padding(16.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.dialog_cancel),
                        style =
                            TextStyle.Default.copy(
                                fontSize = 16.sp,
                                color = EBookTheme.colors.colorPrimary,
                            ),
                        modifier =
                            Modifier.clickable {
                                onDismissRequest()
                            }
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 24.dp))
                    Text(
                        text = stringResource(R.string.dialog_ok),
                        style =
                            TextStyle.Default.copy(
                                fontSize = 16.sp,
                                color = EBookTheme.colors.colorPrimary,
                            ),
                        modifier =
                            Modifier
                                .padding(end = 16.dp)
                                .clickable {
                                    onRemoveSearchRecord(searchRecord)
                                    onDismissRequest()
                                }
                    )
                }
            }
        }
    }
}

//region Previews
@Preview(
    name = "Book Search Item Light",
    group = "component",
    showBackground = true,
    showSystemUi = false,
    device = MDPI_DEVICES
)
@Preview(
    name = "Book Search Item Dark",
    group = "component",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = MDPI_DEVICES
)
@Composable
private fun BookHeaderPreview() {
    EBookTheme {
        ConfirmRemoveRecordDialog(
            searchRecord =
                SearchRecord(
                    id = 777,
                    times = 3446,
                    text = "vocibus"
                ),
            onDismissRequest = {}
        )
    }
}
//endregion
