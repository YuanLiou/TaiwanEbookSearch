package liou.rayyuan.ebooksearchtaiwan.booksearch.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.ui.theme.compatibleSafeDrawingWindowInsets
import liou.rayyuan.ebooksearchtaiwan.ui.theme.pale_slate

@Composable
fun DetailPaneEmptyState(modifier: Modifier = Modifier,) {
    Scaffold(
        contentWindowInsets = compatibleSafeDrawingWindowInsets(),
        modifier = modifier,
    ) { paddings ->
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(pale_slate)
                    .fillMaxSize()
                    .consumeWindowInsets(paddings),
        ) {
            Image(
                painter = painterResource(id = R.drawable.big_icon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier =
                    Modifier
                        .size(200.dp)
                        .alpha(0.3f),
            )
        }
    }
}
