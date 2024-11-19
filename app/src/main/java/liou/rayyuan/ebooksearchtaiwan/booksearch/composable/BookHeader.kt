package liou.rayyuan.ebooksearchtaiwan.booksearch.composable

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Column
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@Composable
fun BookHeader(
    subtitle: String,
    modifier: Modifier = Modifier,
    showStatusText: Boolean = false,
    statusText: String? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth(),
    ) {
        Spacer(
            modifier =
                Modifier
                    .height(3.dp)
                    .width(70.dp)
                    .background(EBookTheme.colors.dividerColor)
                    .padding(top = 24.dp)
        )
        Text(
            modifier = Modifier.padding(vertical = 12.dp),
            text = subtitle,
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.secondary
                ),
            textAlign = TextAlign.Center,
        )
        if (showStatusText) {
            val status = statusText ?: stringResource(R.string.result_nothing)
            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = status,
                style =
                    MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    ),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

//region Previews
@Preview(
    name = "Book Header Light",
    group = "component",
    showBackground = true,
    showSystemUi = false,
    apiLevel = 34,
    device = MDPI_DEVICES
)
@Preview(
    name = "Book Header Dark",
    group = "component",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = 34,
    device = MDPI_DEVICES
)
@Composable
private fun BookHeaderPreview() {
    EBookTheme {
        BookHeader(
            subtitle = "This is a subtitle",
            showStatusText = true
        )
    }
}
//endregion
