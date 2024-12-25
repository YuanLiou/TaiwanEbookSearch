package liou.rayyuan.ebooksearchtaiwan.booksearch.composable

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.allowRgb565
import coil3.request.crossfade
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation
import com.rayliu.commonmain.domain.model.Book
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.utils.resolveColorAttribute
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.BookUiModel
import liou.rayyuan.ebooksearchtaiwan.booksearch.list.asUiModel

@Composable
fun BookSearchItem(
    uiModel: BookUiModel,
    modifier: Modifier = Modifier,
    onBookSearchItemClick: (Book) -> Unit = {}
) {
    Card(
        colors =
            CardDefaults.cardColors().copy(
                containerColor = EBookTheme.colors.cardBackgroundColor
            ),
        shape = RoundedCornerShape(dimensionResource(R.dimen.round_corner)),
        modifier =
            modifier
                .fillMaxWidth()
                .clickable {
                    onBookSearchItemClick(uiModel.book)
                }
    ) {
        val context = LocalContext.current
        val placeholder = rememberAsyncImagePainter(ContextCompat.getDrawable(context, R.drawable.book_image_placeholder))
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!uiModel.book.isFirstChoice) {
                Text(
                    text = uiModel.getShopName(context),
                    style =
                        TextStyle.Default.copy(
                            color = resolveColorAttribute(context, android.R.attr.textColorSecondary, EBookTheme.colors.subtitle1TextColor),
                            fontWeight = FontWeight.Light
                        ),
                    textAlign = TextAlign.Start
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model =
                        ImageRequest.Builder(LocalContext.current)
                            .data(uiModel.getImage())
                            .crossfade(true)
                            .allowRgb565(true)
                            .transformations(
                                RoundedCornersTransformation(LocalContext.current.resources.getDimension(R.dimen.image_round_corner))
                            )
                            .build(),
                    contentDescription = "book cover image",
                    placeholder = placeholder,
                    contentScale = ContentScale.Fit,
                    modifier =
                        Modifier
                            .width(dimensionResource(R.dimen.list_book_cover_width))
                            .height(dimensionResource(R.dimen.list_book_cover_height))
                            .clip(RoundedCornerShape(dimensionResource(R.dimen.image_round_corner)))
                )
                Column(
                    modifier = Modifier.padding(start = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = uiModel.getTitle(),
                        style =
                            TextStyle.Default.copy(
                                color =
                                    resolveColorAttribute(
                                        context,
                                        android.R.attr.textColorPrimary,
                                        EBookTheme.colors.textColorTertiary
                                    ),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                            ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = uiModel.getDescription(),
                        style =
                            TextStyle.Default.copy(
                                color =
                                    resolveColorAttribute(
                                        context,
                                        android.R.attr.textColorSecondary,
                                        EBookTheme.colors.textColorTertiary
                                    ),
                                fontWeight = FontWeight.Light,
                                fontSize = 14.sp,
                                // 1.2
                                lineHeight = (16.8).sp
                            ),
                        maxLines = 6,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Text(
                text = uiModel.getPrice(),
                style =
                    TextStyle.Default.copy(
                        color = EBookTheme.colors.colorPrimary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                    ),
                textAlign = TextAlign.End,
                modifier =
                    Modifier
                        .padding(end = 8.dp)
                        .align(Alignment.End)
            )
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
        BookSearchItem(Book.DUMMY_BOOK.asUiModel())
    }
}
//endregion
