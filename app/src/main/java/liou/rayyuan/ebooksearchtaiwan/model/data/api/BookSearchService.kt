package liou.rayyuan.ebooksearchtaiwan.model.data.api

import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkCrawerResult

interface BookSearchService {
    suspend fun postBooks(keyword: String): NetworkCrawerResult
    suspend fun postBooks(stores: List<String>, keyword: String): NetworkCrawerResult
}