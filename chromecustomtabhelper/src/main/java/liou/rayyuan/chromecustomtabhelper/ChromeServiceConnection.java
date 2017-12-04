package liou.rayyuan.chromecustomtabhelper;

import android.content.ComponentName;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsServiceConnection;

import java.lang.ref.WeakReference;

/**
 * Created by louis383 on 2017/1/20.
 */

public class ChromeServiceConnection extends CustomTabsServiceConnection {

    private WeakReference<ChromeServiceConnection.Callback> callback;

    public ChromeServiceConnection(ChromeServiceConnection.Callback callback) {
        this.callback = new WeakReference<>(callback);
    }

    @Override
    public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
        ChromeServiceConnection.Callback serviceConnectionCallback = callback.get();
        if (serviceConnectionCallback != null) {
            serviceConnectionCallback.onServiceConnected(client);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        ChromeServiceConnection.Callback serviceConnectionCallback = callback.get();
        if (serviceConnectionCallback != null) {
            serviceConnectionCallback.onServiceDisconnected();
        }
    }

    public interface Callback {
        void onServiceConnected(CustomTabsClient client);
        void onServiceDisconnected();
    }
}
