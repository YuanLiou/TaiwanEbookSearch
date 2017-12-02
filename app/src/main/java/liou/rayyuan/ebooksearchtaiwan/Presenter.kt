package liou.rayyuan.ebooksearchtaiwan

/**
 * Created by louis383 on 2017/12/2.
 */

interface Presenter<V> {
    fun attachView(view: V)
    fun detachView()
}
