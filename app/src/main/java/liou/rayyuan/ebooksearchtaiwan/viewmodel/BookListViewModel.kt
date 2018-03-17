package liou.rayyuan.ebooksearchtaiwan.viewmodel

import android.arch.lifecycle.ViewModel
import liou.rayyuan.ebooksearchtaiwan.model.APIManager
import liou.rayyuan.ebooksearchtaiwan.model.NetworkLiveData
import liou.rayyuan.ebooksearchtaiwan.model.entity.BookStores

class BookListViewModel: ViewModel() {

    private var liveData: NetworkLiveData<BookStores>? = null
    var apiManager: APIManager? = null

    fun getBookList(aboutSomething: String, force: Boolean): NetworkLiveData<BookStores>? {
        if (apiManager == null) {
            return null
        }

        if (force || (liveData == null && aboutSomething.isNotEmpty())) {
            liveData = apiManager!!.getBooks(aboutSomething)
            liveData?.requestData()
        }

        return liveData
    }

    override fun onCleared() {
        liveData?.cancel()
    }
}