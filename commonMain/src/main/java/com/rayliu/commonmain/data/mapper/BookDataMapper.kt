package com.rayliu.commonmain.data.mapper

import com.rayliu.commonmain.LevenshteinDistanceHelper
import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.data.dto.NetworkBook
import com.rayliu.commonmain.data.mapper.basic.Mapper
import com.rayliu.commonmain.domain.model.Book
import java.util.regex.Pattern

class BookDataMapper(
    private val levenshteinDistance: LevenshteinDistanceHelper
) : Mapper<NetworkBook, Book> {

    private var currentBookStore: DefaultStoreNames = DefaultStoreNames.UNKNOWN
    private var keywords: String = ""
    private val chineseCharacterPattern = Pattern.compile(
        "([\\u4E00-\\u9FFF]|([：？！]))\\s+([\\u4E00-\\u9FFF]|([：？！]))"
    )

    fun setupBookStore(store: DefaultStoreNames) {
        currentBookStore = store
    }

    fun setupKeywords(keywords: String) {
        this.keywords = keywords
    }

    override fun map(input: NetworkBook): Book {
        return with(input) {
            val bookTitle = title?.removeSpaces() ?: ""
            Book(
                thumbnail = thumbnail.orEmpty(),
                priceCurrency = priceCurrency ?: "TWD",
                price = price ?: 0.0f,
                link = link.orEmpty(),
                about = about?.removeSpaces() ?: "",
                id = id.orEmpty(),
                title = bookTitle,
                authors = authors.orEmpty(),
                bookStore = currentBookStore,
                titleKeywordSimilarity = run {
                    if (keywords.isEmpty() || bookTitle.isEmpty()) {
                        return@run null
                    }
                    levenshteinDistance.check(original = keywords, target = bookTitle)
                }
            )
        }
    }

    private fun String.removeSpaces(): String {
        val trimmedString = this.trim()
        return chineseCharacterPattern.matcher(trimmedString).replaceAll("$1$2")
    }
}
