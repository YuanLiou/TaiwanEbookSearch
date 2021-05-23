package liou.rayyuan.ebooksearchtaiwan.model.data.mapper

import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkCrawerResult
import liou.rayyuan.ebooksearchtaiwan.model.data.mapper.basic.Mapper
import liou.rayyuan.ebooksearchtaiwan.model.domain.model.SearchResult

class SearchResultMapper(
    private val bookStoreListMapper: BookStoreListMapper
) : Mapper<NetworkCrawerResult, SearchResult> {

    override fun map(input: NetworkCrawerResult): SearchResult {
        return SearchResult(
            keyword = input.keywords ?: "",
            apiVersion = input.apiVersion ?: "",
            bookStores = bookStoreListMapper.map(input.networkResults),
            totalBookCounts = input.totalQuantity ?: 0
        )
    }
}