package liou.rayyuan.ebooksearchtaiwan.booksearch.composable

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.uimodel.BookUiModel

@Composable
fun BookItem(
    uiModel: BookUiModel,
    modifier: Modifier = Modifier
) {
    // TODO: Book Item Composable

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
        BookItem(BookUiModel.DUMMY_DATA)
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
