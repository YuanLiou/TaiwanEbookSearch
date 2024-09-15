package com.rayliu.commonmain.data.mapper

import com.rayliu.commonmain.data.DefaultStoreNames
import com.rayliu.commonmain.data.dto.NetworkCrawerResult
import com.rayliu.commonmain.data.mapper.basic.Mapper
import com.rayliu.commonmain.domain.model.SearchResult

class SearchResultMapper(
    private val networkResultToBookStoreListMapper: NetworkResultToBookStoreListMapper
) : Mapper<NetworkCrawerResult, SearchResult> {
    fun setEnableStores(enableStores: List<DefaultStoreNames>) {
        networkResultToBookStoreListMapper.setEnableStores(enableStores)
    }

    override fun map(input: NetworkCrawerResult): SearchResult {
        val keywords = input.keywords.orEmpty()
        networkResultToBookStoreListMapper.setKeywords(keywords)
        return SearchResult(
            keyword = keywords,
            apiVersion = input.apiVersion ?: "",
            bookStores = networkResultToBookStoreListMapper.map(input.networkResults),
            totalBookCounts = input.totalQuantity ?: 0
        )
    }
}
