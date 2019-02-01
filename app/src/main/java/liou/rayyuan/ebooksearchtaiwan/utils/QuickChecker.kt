package liou.rayyuan.ebooksearchtaiwan.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import liou.rayyuan.ebooksearchtaiwan.R

class QuickChecker(context: Context) {

    private val context = context.applicationContext

    fun isTabletSize(): Boolean = context.resources.getBoolean(R.bool.isTabletSize)

    fun isInternetConnectionAvailable(): Boolean {
        val connectionManager: ConnectivityManager = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectionManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected && networkInfo.isAvailable
    }

}