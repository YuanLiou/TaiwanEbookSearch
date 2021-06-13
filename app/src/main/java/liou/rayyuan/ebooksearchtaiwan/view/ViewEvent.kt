package liou.rayyuan.ebooksearchtaiwan.view

import androidx.lifecycle.Observer

class ViewEvent<out T>(private val content: T) {

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

class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit): Observer<ViewEvent<T>> {
    override fun onChanged(event: ViewEvent<T>?) {
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}