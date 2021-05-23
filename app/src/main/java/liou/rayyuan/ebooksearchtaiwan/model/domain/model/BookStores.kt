package liou.rayyuan.ebooksearchtaiwan.model.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class BookStores(val booksCompany: List<Book>?,
                      val readmoo: List<Book>?,
                      val kobo: List<Book>?,
                      val taaze: List<Book>?,
                      val bookWalker: List<Book>?,
                      val playStore: List<Book>?,
                      val pubu: List<Book>?,
                      val hyread: List<Book>?,
                      val kindle: List<Book>?
) : Parcelable
