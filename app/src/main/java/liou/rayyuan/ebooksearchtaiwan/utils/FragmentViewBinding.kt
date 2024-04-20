package liou.rayyuan.ebooksearchtaiwan.utils

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBinding<T : ViewBinding>(
    private val bindingAction: (View) -> T
) : ReadOnlyProperty<Fragment, T> {
    private var viewBinding: T? = null
    private val lifecycleObserver = BindingLifecycleObserver()

    override fun getValue(
        thisRef: Fragment,
        property: KProperty<*>
    ): T {
        this.viewBinding?.let {
            return it
        }
        val view = thisRef.requireView()
        thisRef.viewLifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        return bindingAction(view).also {
            this.viewBinding = it
        }
    }

    private inner class BindingLifecycleObserver : DefaultLifecycleObserver {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        @MainThread
        override fun onDestroy(owner: LifecycleOwner) {
            owner.lifecycle.removeObserver(this)
            // Fragment.viewLifecycleOwner call LifecyclerObserver.onDestroy before Fragment.onDestroyView().
            // That's why we need to postpone reset of the viewBinding
            mainThreadHandler.post {
                this@FragmentViewBinding.viewBinding = null
            }
        }
    }
}
