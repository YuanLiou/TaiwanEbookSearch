package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.net.Uri

/**
 * Created by louis383 on 2017/12/2.
 */

interface BookSearchView {
    fun setupInterface()
    fun bookCompanyIsEmpty()
    fun readmooIsEmpty()
    fun koboIsEmpty()
    fun taazeIsEmpty()
    fun openBookLink(uri: Uri)
}
