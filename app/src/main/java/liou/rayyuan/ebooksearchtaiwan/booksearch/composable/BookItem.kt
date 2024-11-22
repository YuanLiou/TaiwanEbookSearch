package liou.rayyuan.ebooksearchtaiwan.booksearch.composable

import android.content.res.Configuration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.uimodel.BookUiModel

@Composable
fun BookItem(
    uiModel: BookUiModel,
    modifier: Modifier = Modifier,
    onBookCardClick: (book: BookUiModel) -> Unit = {}
) {
    /* UI Structure
    [CARD](RoundShape, radius 10dp)
        [COLUMN](padding h = 12dp, v = 8dp)
            [ROW](gravity center vertical, fill width)
                [TEXT](uiModel.getShopName)(weight 1f)(font weight light)
            [ROW](gravity center vertical, fill width, height IntrinsicSize.Min, spaceBy 8dp, padding top 8dp)
                [AsyncImage from Coil3](uiModel.getImage)(Round corner 4dp)
                [COLUMN](spaceBy 6dp, fill height)
                    [TEXT](uiModel.getTitle)(18sp bold ellipsis)
                    [TEXT](uiModel.getDescription)(14sp bold ellipsis)
            [TEXT](uiModel.getPrice)(align end)(16sp)
     */

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EBookTheme.colors.cardBackgroundColor),
        modifier =
            modifier
                .clickable {
                    onBookCardClick(uiModel)
                }
                .fillMaxWidth()
                .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val context = LocalContext.current
                Text(
                    modifier = Modifier.weight(1f),
                    text = uiModel.getShopName(context),
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = EBookTheme.colors.textColorTertiary,
                            fontWeight = FontWeight.Light
                        ),
                    textAlign = TextAlign.Start,
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_more_vert_black_24dp),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                )
            }
            Row(
                modifier = Modifier.height(IntrinsicSize.Min).padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val inPreviewWindow = LocalInspectionMode.current
                if (inPreviewWindow) {
                    Spacer(
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Gray)
                                .width(dimensionResource(R.dimen.list_book_cover_width))
                                .height(dimensionResource(R.dimen.list_book_cover_height))
                    )
                } else {
                    AsyncImage(
                        model = uiModel.getImage(),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .width(dimensionResource(R.dimen.list_book_cover_width))
                                .height(dimensionResource(R.dimen.list_book_cover_height))
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = uiModel.getTitle(),
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                        textAlign = TextAlign.Start,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = uiModel.getDescription(),
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp
                            ),
                        textAlign = TextAlign.Start,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Text(
                modifier = Modifier.align(Alignment.End),
                text = uiModel.getPrice(),
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        color = EBookTheme.colors.colorPrimary,
                        fontSize = 16.sp
                    ),
                textAlign = TextAlign.Start,
            )
        }
    }
}

//region Previews
@Preview(
    name = "Book Item Light",
    group = "component",
    showBackground = true,
    showSystemUi = false,
    device = MDPI_DEVICES
)
@Preview(
    name = "Book Item Dark",
    group = "component",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = MDPI_DEVICES
)
@Composable
private fun BookItemPreview() {
    EBookTheme {
        BookItem(
            uiModel = BookUiModel.DUMMY_DATA
        )
    }
}

@Preview(
    name = "Book Item Light",
    group = "preview",
    showBackground = false,
    showSystemUi = false,
    widthDp = 320,
    heightDp = 480
)
@Composable
private fun BookItemSamplePreview() {
    EBookTheme {
        Box {
            Image(
                painter = painterResource(id = R.drawable.ic_img_previews),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
        }
    }
}
//endregion
