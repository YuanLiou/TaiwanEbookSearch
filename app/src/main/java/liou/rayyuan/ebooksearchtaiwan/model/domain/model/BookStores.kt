package liou.rayyuan.ebooksearchtaiwan.model.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class BookStores(val booksCompany: BookResult?,
                      val readmoo: BookResult?,
                      val kobo: BookResult?,
                      val taaze: BookResult?,
                      val bookWalker: BookResult?,
                      val playStore: BookResult?,
                      val pubu: BookResult?,
                      val hyread: BookResult?,
                      val kindle: BookResult?
) : Parcelable
