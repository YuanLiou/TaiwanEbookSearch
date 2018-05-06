package liou.rayyuan.ebooksearchtaiwan.viewmodel

import android.arch.lifecycle.ViewModel
import android.util.Log
import liou.rayyuan.ebooksearchtaiwan.model.APIManager
import liou.rayyuan.ebooksearchtaiwan.model.NetworkLiveData
import liou.rayyuan.ebooksearchtaiwan.model.entity.BookStores

class BookListViewModel: ViewModel() {

    private var liveData: NetworkLiveData<BookStores>? = null
    var apiManager: APIManager? = null

    fun getBookList(aboutSomething: String, force: Boolean): NetworkLiveData<BookStores>? {
        requireNotNull(apiManager, { Log.e("BookListViewModel", "APIManager can't be null")})

        if (force || (liveData == null && aboutSomething.isNotEmpty())) {
            liveData = apiManager?.getBooks(aboutSomething)
            liveData?.requestData()
        }
        return liveData
    }

    override fun onCleared() {
        Log.i("BookListViewModel", "onCleared.")
        forceStop()
    }

    fun isRequestingData(): Boolean {
        return liveData?.isConnecting() == true
    }

    fun forceStop() {
        liveData?.cancel()
    }
}