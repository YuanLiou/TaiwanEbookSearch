package liou.rayyuan.ebooksearchtaiwan.booksearch.composable

import androidx.compose.ui.unit.dp
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = EBookTheme.colors.cardBackgroundColor,
            ),
        modifier =
            modifier
                .fillMaxWidth()
                .padding(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.advertisement_title),
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        color = EBookTheme.colors.subtitle1TextColor
                    ),
                textAlign = TextAlign.Center
            )
            AdView()
        }
    }
}

@Composable
private fun AdView(modifier: Modifier = Modifier) {
    if (LocalInspectionMode.current) {
        Text(
            modifier = Modifier,
            text = "AdView",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Start,
        )
        return
    }

    val unitId = stringResource(R.string.AD_MOB_UNIT_ID)
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = unitId
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

@Preview(
    name = "AdBanner Card",
    group = "component",
    showSystemUi = false,
    apiLevel = 34,
    device = MDPI_DEVICES
)
@Preview(
    name = "AdBanner Dark",
    group = "component",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = 34,
    device = MDPI_DEVICES,
)
@Composable
private fun AdBannerPreview() {
    EBookTheme {
        AdBanner()
    }
}
