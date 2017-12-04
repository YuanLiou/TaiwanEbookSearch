package liou.rayyuan.chromecustomtabhelper;

import android.app.Activity;
import android.net.Uri;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.util.Log;

/**
 * Created by louis383 on 2017/1/20.
 */

public class ChromeCustomTabsHelper implements ChromeServiceConnection.Callback {

    private CustomTabsClient customTabsClient;
    private CustomTabsServiceConnection customTabsServiceConnection;

    public static void openCustomTab(Activity activity, CustomTabsIntent customTabsIntent, Uri uri, Fallback fallback) {
        String packageName = ChromeCustomTabsUtils.getPackageNameToUse(activity, uri.toString());

        if (packageName == null) {
            // Chrome doesn't installed.
            if (fallback != null) {
                fallback.openWithWebView(activity, uri);
            }
        } else {
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(activity, uri);
        }
    }

    public void bindCustomTabsServices(Activity activity, String hostURL) {
        if (customTabsClient != null) {
            return;
        }

        String packageName = ChromeCustomTabsUtils.getPackageNameToUse(activity, hostURL);
        Log.i("ChromeCustomTabsHelper", "bound packageName: " + packageName);
        if (packageName == null) {
            return;
        }

        customTabsServiceConnection = new ChromeServiceConnection(this);
        CustomTabsClient.bindCustomTabsService(activity, packageName, customTabsServiceConnection);

        Log.i("ChromeCustomTabsHelper", "bindCustomTabsServices");
    }

    public void unbindCustomTabsServices(Activity activity) {
        if (customTabsServiceConnection == null) {
            return;
        }

        activity.unbindService(customTabsServiceConnection);
        customTabsClient = null;
        customTabsServiceConnection = null;

        Log.i("ChromeCustomTabsHelper", "unbindCustomTabsServices");
    }

    @Override
    public void onServiceConnected(CustomTabsClient client) {
        customTabsClient = client;
        customTabsClient.warmup(0L);

        Log.i("ChromeCustomTabsHelper", "onServiceConnected");
    }

    @Override
    public void onServiceDisconnected() {
        customTabsClient = null;

        Log.i("ChromeCustomTabsHelper", "onServiceDisconnected");
    }

    public interface Fallback {
        void openWithWebView(Activity activity, Uri uri);
    }
}
