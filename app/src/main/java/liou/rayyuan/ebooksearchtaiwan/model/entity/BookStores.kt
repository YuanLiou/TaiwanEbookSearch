package liou.rayyuan.ebooksearchtaiwan.model.entity

import kotlinx.serialization.Serializable

/*
*
{
  "booksCompany": [],
  "readmoo": [],
  "kobo": [],
  "taaze": [],
  "bookWalker": [],
  "playStore": [],
  "pubu": [],
  "hyread": []
}
* }
*/
@Serializable
data class BookStores(val booksCompany: List<Book>? = null,
                      val readmoo: List<Book>? = null,
                      val kobo: List<Book>? = null,
                      val taaze: List<Book>? = null,
                      val bookWalker: List<Book>? = null,
                      val playStore: List<Book>? = null,
                      val pubu: List<Book>? = null,
                      val hyread: List<Book>? = null)
