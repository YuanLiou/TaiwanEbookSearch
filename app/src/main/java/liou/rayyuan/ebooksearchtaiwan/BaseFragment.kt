package liou.rayyuan.ebooksearchtaiwan

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment(
    @LayoutRes contentLayoutId: Int = 0
) : Fragment(contentLayoutId) {
    protected fun isDarkTheme(): Boolean = (requireActivity() as? BaseActivity)?.isDarkTheme() ?: false
}
