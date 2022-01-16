package liou.rayyuan.ebooksearchtaiwan

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import liou.rayyuan.ebooksearchtaiwan.model.EventTracker
import com.rayliu.commonmain.domain.service.UserPreferenceManager
import org.koin.android.ext.android.inject

abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    protected val eventTracker: EventTracker by inject()

    protected fun isDarkTheme(): Boolean {
        return (requireActivity() as? BaseActivity)?.isDarkTheme() ?: false
    }
}