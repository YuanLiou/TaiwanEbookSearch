package liou.rayyuan.ebooksearchtaiwan.view

import androidx.lifecycle.Observer

class ViewEffect<out T>(private val content: T) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T {
        return content
    }
}

class ViewEffectObserver<T>(private val onEventUnhandledContent: (T) -> Unit): Observer<ViewEffect<T>> {
    override fun onChanged(effect: ViewEffect<T>?) {
        effect?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}