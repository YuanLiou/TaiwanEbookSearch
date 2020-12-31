package liou.rayyuan.ebooksearchtaiwan.model.domain.model


data class BookStores(val booksCompany: List<Book>?,
                      val readmoo: List<Book>?,
                      val kobo: List<Book>?,
                      val taaze: List<Book>?,
                      val bookWalker: List<Book>?,
                      val playStore: List<Book>?,
                      val pubu: List<Book>?,
                      val hyread: List<Book>?)
