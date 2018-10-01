package liou.rayyuan.ebooksearchtaiwan.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import liou.rayyuan.ebooksearchtaiwan.model.APIManager
import liou.rayyuan.ebooksearchtaiwan.model.NetworkLiveData
import liou.rayyuan.ebooksearchtaiwan.model.OnNetworkConnectionListener
import liou.rayyuan.ebooksearchtaiwan.model.entity.BookStores

class BookListViewModel: ViewModel() {

    private var bookStoresLiveData: NetworkLiveData<BookStores>? = null
    var networkConnectionListener: OnNetworkConnectionListener? = null
    var apiManager: APIManager? = null

    fun getBookList(aboutSomething: String = "", force: Boolean): NetworkLiveData<BookStores>? {
        requireNotNull(apiManager) {
            Log.e("BookListViewModel", "APIManager can't be null")
        }

        if (force || (bookStoresLiveData == null && aboutSomething.isNotEmpty())) {
            bookStoresLiveData = apiManager?.getBooks(aboutSomething)
            bookStoresLiveData?.listener = networkConnectionListener
            bookStoresLiveData?.requestData()
        }
        return bookStoresLiveData
    }

    override fun onCleared() {
        Log.i("BookListViewModel", "onCleared.")
        forceStop()
    }

    fun isRequestingData(): Boolean {
        return bookStoresLiveData?.isConnecting() == true
    }

    fun forceStop() {
        bookStoresLiveData?.cancel()
    }
}