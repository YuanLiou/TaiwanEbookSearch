package liou.rayyuan.ebooksearchtaiwan.model.entity

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
data class BookStores(val booksCompany: List<Book>?,
                      val readmoo: List<Book>?,
                      val kobo: List<Book>?,
                      val taaze: List<Book>?,
                      val bookWalker: List<Book>?,
                      val playStore: List<Book>?,
                      val pubu: List<Book>?,
                      val hyread: List<Book>?)
