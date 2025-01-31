package liou.rayyuan.ebooksearchtaiwan.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class NetworkChecker(
    context: Context
) {
    private val context = context.applicationContext

    @Suppress("DEPRECATION")
    fun isInternetConnectionAvailable(): Boolean {
        val connectionManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (connectionManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capability = connectionManager.getNetworkCapabilities(connectionManager.activeNetwork) ?: return false
                if (capability.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true
                }
                if (capability.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                }
                return capability.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } else {
                val networkInfo = connectionManager.getActiveNetworkInfo()
                return networkInfo?.isConnected() == true && networkInfo.isAvailable()
            }
        }
        return false
    }
}
