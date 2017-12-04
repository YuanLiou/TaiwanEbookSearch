package liou.rayyuan.ebooksearchtaiwan.model.entity

/*
* {
   "booksCompany":[ ],
   "readmoo":[ ],
   "kobo":[ ],
   "taaze":[ ]
   "bookWalker": []
* }
 */
data class BookStores(val booksCompany: List<Book>?,
                      val readmoo: List<Book>?,
                      val kobo: List<Book>?,
                      val taaze: List<Book>?,
                      val bookWalker: List<Book>? )