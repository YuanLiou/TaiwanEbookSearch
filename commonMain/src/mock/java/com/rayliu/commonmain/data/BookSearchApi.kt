package com.rayliu.commonmain.data

import com.rayliu.commonmain.data.api.BookSearchService
import com.rayliu.commonmain.data.dto.NetworkCrawerResult

class BookSearchApi : BookSearchService {

    override suspend fun postBooks(keyword: String): NetworkCrawerResult {
        return NetworkCrawerResult()
    }

    override suspend fun postBooks(stores: List<String>, keyword: String): NetworkCrawerResult {
        return NetworkCrawerResult()
    }

    override suspend fun getSearchSnapshot(searchId: String): NetworkCrawerResult {
        return NetworkCrawerResult()
    }
}