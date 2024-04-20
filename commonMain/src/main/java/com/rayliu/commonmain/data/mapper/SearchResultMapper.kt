package com.rayliu.commonmain.data.mapper

import com.rayliu.commonmain.data.dto.NetworkCrawerResult
import com.rayliu.commonmain.data.mapper.basic.Mapper
import com.rayliu.commonmain.domain.model.SearchResult

class SearchResultMapper(
    private val bookStoreListMapper: BookStoreListMapper
) : Mapper<NetworkCrawerResult, SearchResult> {
    override fun map(input: NetworkCrawerResult): SearchResult {
        val keywords = input.keywords.orEmpty()
        bookStoreListMapper.setKeywords(keywords)
        return SearchResult(
            keyword = keywords,
            apiVersion = input.apiVersion ?: "",
            bookStores = bookStoreListMapper.map(input.networkResults),
            totalBookCounts = input.totalQuantity ?: 0
        )
    }
}
