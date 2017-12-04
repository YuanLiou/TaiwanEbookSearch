package liou.rayyuan.chromecustomtabhelper;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by louis383 on 2017/1/20.
 */

public class ChromeCustomTabsUtils {

    private static final String STABLE_PACKAGE = "com.android.chrome";
    private static final String BETA_PACKAGE = "com.chrome.beta";
    private static final String DEV_PACKAGE = "com.chrome.dev";
    private static final String CANARY_PACKAGE = "com.chrome.canary";
    private static final String LOCAL_PACKAGE = "com.google.android.apps.chrome";

    private static final String ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService";

    private static String packageNameToUse;

    public static String getPackageNameToUse(Context context, String urlString) {
        if (packageNameToUse != null) {
            return packageNameToUse;
        }

        PackageManager packageManager = context.getPackageManager();
        // get default VIEW intent handler
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
        ResolveInfo defaultViewHandlerInfo = packageManager.resolveActivity(intent, 0);
        String defaultViewHandlerPackageName = null;
        if (defaultViewHandlerInfo != null) {
            defaultViewHandlerPackageName = defaultViewHandlerInfo.activityInfo.packageName;
        }

        // get all apps that can handle VIEW intent
        List<ResolveInfo> resolvedActivityList = packageManager.queryIntentActivities(intent, 0);
        List<String> packagesSupportingCustomTabs = new ArrayList<>();
        for (ResolveInfo resolveInfo : resolvedActivityList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction(ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(resolveInfo.activityInfo.packageName);
            if (packageManager.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(resolveInfo.activityInfo.packageName);
            }
        }

        // packagesSupportingCustomTabs contains all apps that can handle both VIEW intent
        // and service call
        if (packagesSupportingCustomTabs.isEmpty()) {
            return null;
        } else if (packagesSupportingCustomTabs.size() == 1) {
            packageNameToUse = packagesSupportingCustomTabs.get(0);
        } else if (!TextUtils.isEmpty(defaultViewHandlerPackageName) &&
                !hasSpecializeHandlerIntent(context, intent) &&
                packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)) {
            packageNameToUse = defaultViewHandlerPackageName;
        } else if (packagesSupportingCustomTabs.contains(STABLE_PACKAGE)) {
            packageNameToUse = STABLE_PACKAGE;
        } else if (packagesSupportingCustomTabs.contains(BETA_PACKAGE)) {
            packageNameToUse = BETA_PACKAGE;
        } else if (packagesSupportingCustomTabs.contains(DEV_PACKAGE)) {
            packageNameToUse = DEV_PACKAGE;
        } else if (packagesSupportingCustomTabs.contains(CANARY_PACKAGE)) {
            packageNameToUse = CANARY_PACKAGE;
        } else if (packagesSupportingCustomTabs.contains(LOCAL_PACKAGE)) {
            packageNameToUse = LOCAL_PACKAGE;
        }

        return packageNameToUse;
    }

    private static boolean hasSpecializeHandlerIntent(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> handlers = packageManager.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);
        if (handlers == null || handlers.size() == 0) {
            return false;
        }

        for (ResolveInfo info : handlers) {
            IntentFilter filter = info.filter;
            if (filter == null) {
                continue;
            }

            if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) {
                continue;
            }

            if (info.activityInfo == null) {
                continue;
            }

            return true;
        }

        return false;
    }
}
