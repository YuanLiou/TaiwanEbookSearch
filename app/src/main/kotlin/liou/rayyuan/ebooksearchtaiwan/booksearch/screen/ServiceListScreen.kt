package liou.rayyuan.ebooksearchtaiwan.booksearch.screen

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rayliu.commonmain.domain.model.BookStoreDetails
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.ServiceStatusList

@Composable
fun ServiceListScreen(
    appVersion: String,
    modifier: Modifier = Modifier,
    bookStoreDetails: ImmutableList<BookStoreDetails> = persistentListOf(),
    contentPaddings: PaddingValues = PaddingValues()
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ServiceStatusList(
            storeDetails = bookStoreDetails,
            contentPaddings = contentPaddings
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.app_version, appVersion),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}
