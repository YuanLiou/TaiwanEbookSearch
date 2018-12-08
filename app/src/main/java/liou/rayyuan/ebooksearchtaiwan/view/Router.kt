package liou.rayyuan.ebooksearchtaiwan.view

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class Router(private val fragmentManager: FragmentManager,
             @IdRes private val containerId: Int) {

    fun replaceView(fragment: Fragment, tag: String?) {
        fragmentManager.beginTransaction()
                .replace(containerId, fragment, tag)
                .commit()
    }

    fun findFragmentByTag(tag: String): Fragment? {
        return fragmentManager.findFragmentByTag(tag)
    }

    fun findTopFragment(): Fragment? = fragmentManager.findFragmentById(containerId)

}