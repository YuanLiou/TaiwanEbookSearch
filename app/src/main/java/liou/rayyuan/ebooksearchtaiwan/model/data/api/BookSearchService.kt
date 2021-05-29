package liou.rayyuan.ebooksearchtaiwan.model.data.api

import liou.rayyuan.ebooksearchtaiwan.model.data.dto.NetworkCrawerResult

interface BookSearchService {

    // Query("q")
    suspend fun postBooks(keyword: String): NetworkCrawerResult

}