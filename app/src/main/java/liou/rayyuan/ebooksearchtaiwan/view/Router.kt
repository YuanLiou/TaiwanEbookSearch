package liou.rayyuan.ebooksearchtaiwan.view

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class Router(private val fragmentManager: FragmentManager,
             @IdRes private val containerId: Int) {

    fun replaceView(fragment: Fragment, tag: String?) {
        fragmentManager.beginTransaction()
                .replace(containerId, fragment, tag)
                .commit()
    }

    fun addView(fragment: Fragment, tag: String?, addToBackStack: Boolean) {
        if (fragment.isSameFragment()) {
            return
        }

        if (!tag.isNullOrEmpty()) {
            val targetFragment = findFragmentByTag(tag)
            if (targetFragment != null) {
                return
            }
        }

        val transaction = fragmentManager.beginTransaction()
        val duplicateFragment = findFragmentByTag(fragment.tag ?: fragment.javaClass.simpleName)
        if (duplicateFragment != null) {
            transaction.attach(duplicateFragment)
        } else {
            transaction.add(containerId, fragment, tag)
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

        findTopFragment()?.run {
            transaction.hide(this)
        }

        if (addToBackStack) {
            transaction.addToBackStack(tag)
        }
        transaction.commit()
    }

    fun backToPreviousFragment(): Boolean {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
            return true
        }
        return false
    }

    fun findFragmentByTag(tag: String): Fragment? {
        return fragmentManager.findFragmentByTag(tag)
    }

    fun findTopFragment(): Fragment? = fragmentManager.findFragmentById(containerId)

    private fun Fragment.isSameFragment(): Boolean {
        val tag = this.tag ?: this.javaClass.simpleName
        val topFragment = findTopFragment()
        return topFragment?.let {
            val topFragmentTag = it.tag ?: it.javaClass.simpleName
            topFragmentTag == tag
        } ?: false
    }

}