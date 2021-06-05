package liou.rayyuan.ebooksearchtaiwan.booksearch

import liou.rayyuan.ebooksearchtaiwan.booksearch.list.AdapterItem

/**
 * Created by louis383 on 2017/12/4.
 */
sealed class ListViewState {
    class Prepare(val scrollToTop: Boolean = false) : ListViewState()
    class Ready(val scrollPosition: Int, val adapterItems: List<AdapterItem>) : ListViewState()
    object Error : ListViewState()
}