package liou.rayyuan.ebooksearchtaiwan.camerapreview.permission

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.CircularProgressIndicator
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.msharialsayari.requestpermissionlib.component.RequestPermissions
import com.msharialsayari.requestpermissionlib.model.DialogParams
import com.msharialsayari.requestpermissionlib.model.TextStyleParams
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.ui.GENERAL_BACKGROUND_COLOR
import liou.rayyuan.ebooksearchtaiwan.ui.MDPI_DEVICES
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme

@Composable
fun CameraPermissionScreen(
    onNavigateToPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        onNavigateToPreview()
    } else {
        PermissionRequestView(
            title = stringResource(id = R.string.permission_required_camera),
            onGrantPermission = onNavigateToPreview,
            modifier = modifier
        )
    }
}

@Composable
fun PermissionRequestView(
    title: String,
    onGrantPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLoading by remember { mutableStateOf(true) }
    var askCameraPermission by remember { mutableStateOf(false) }
    if (askCameraPermission) {
        val appName = stringResource(R.string.app_name)
        val permissionName = stringResource(R.string.permission_camera_name)
        val authYourselfMessage = stringResource(R.string.auth_yourself, appName, permissionName)
        RequestPermissions(
            permissions = listOf(Manifest.permission.CAMERA),
            isGranted = onGrantPermission,
            onDone = {
                askCameraPermission = false
            },
            rationalDialogParams =
                DialogParams(
                    title = R.string.permission_request_title,
                    message = R.string.permission_required_camera,
                    positiveButtonText = R.string.dialog_auth,
                    negativeButtonText = R.string.dialog_cancel,
                    backgroundColor = EBookTheme.colors.cardBackgroundColor,
                    titleTextStyle =
                        TextStyleParams(
                            color = EBookTheme.colors.colorOnPrimary
                        ),
                    messageTextStyle =
                        TextStyleParams(
                            color = EBookTheme.colors.colorOnPrimary
                        ),
                    positiveButtonTextStyle =
                        TextStyleParams(
                            color = EBookTheme.colors.colorAccent
                        ),
                    negativeButtonTextStyle =
                        TextStyleParams(
                            color = EBookTheme.colors.colorAccent
                        )
                ),
            deniedDialogParams =
                DialogParams(
                    title = R.string.permission_request_title,
                    messageStr = authYourselfMessage,
                    positiveButtonText = R.string.auth_take_me_there,
                    negativeButtonText = R.string.dialog_cancel,
                    backgroundColor = EBookTheme.colors.cardBackgroundColor,
                    titleTextStyle =
                        TextStyleParams(
                            color = EBookTheme.colors.colorOnPrimary
                        ),
                    messageTextStyle =
                        TextStyleParams(
                            color = EBookTheme.colors.colorOnPrimary
                        ),
                    positiveButtonTextStyle =
                        TextStyleParams(
                            color = EBookTheme.colors.colorAccent
                        ),
                    negativeButtonTextStyle =
                        TextStyleParams(
                            color = EBookTheme.colors.colorAccent
                        )
                )
        )
    }

    LaunchedEffect(key1 = Unit) {
        delay(TimeUnit.SECONDS.toMillis(1L))
        askCameraPermission = true
        showLoading = false
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        if (showLoading) {
            CircularProgressIndicator(
                color = EBookTheme.colors.colorAccent
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier,
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                OutlinedButton(
                    onClick = {
                        askCameraPermission = true
                    }
                ) {
                    Text(
                        style =
                            TextStyle.Default.copy(
                                color = EBookTheme.colors.colorAccent
                            ),
                        text = stringResource(id = R.string.start_grant_permission)
                    )
                }
            }
        }
    }
}

//region Previews
@Preview(
    name = "PermissionRequestView",
    group = "screen",
    backgroundColor = GENERAL_BACKGROUND_COLOR,
    showBackground = true,
    device = MDPI_DEVICES
)
@Composable
private fun PermissionRequestViewPreview() {
    EBookTheme {
        PermissionRequestView(
            title = "Camera Permission Page",
            onGrantPermission = {}
        )
    }
}
//endregion
