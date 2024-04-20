package liou.rayyuan.ebooksearchtaiwan.utils

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ActivityViewBinding<T : ViewBinding>(
    private val bindingAction: (View) -> T,
    @IdRes private val rootViewId: Int
) : ReadOnlyProperty<ComponentActivity, T> {
    private var viewBinding: T? = null
    private val bindingLifecycleObserver = BindingLifecycleObserver()

    override fun getValue(
        thisRef: ComponentActivity,
        property: KProperty<*>
    ): T {
        this.viewBinding?.let {
            return it
        }

        val rootView: View = thisRef.findViewById(rootViewId)
        thisRef.lifecycle.addObserver(bindingLifecycleObserver)
        return bindingAction(rootView).also {
            this.viewBinding = it
        }
    }

    private inner class BindingLifecycleObserver : DefaultLifecycleObserver {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun onDestroy(owner: LifecycleOwner) {
            owner.lifecycle.removeObserver(this)
            mainThreadHandler.post {
                this@ActivityViewBinding.viewBinding = null
            }
        }
    }
}
