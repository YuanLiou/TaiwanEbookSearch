package liou.rayyuan.ebooksearchtaiwan

import androidx.fragment.app.Fragment
import liou.rayyuan.ebooksearchtaiwan.model.RemoteConfigManager
import liou.rayyuan.ebooksearchtaiwan.model.UserPreferenceManager
import org.koin.android.ext.android.inject

abstract class BaseFragment: Fragment() {

    protected val userPreferenceManager: UserPreferenceManager by inject()
    protected val remoteConfigManager: RemoteConfigManager by inject()

    protected fun isDarkTheme(): Boolean = userPreferenceManager.isDarkTheme()

}