package com.rayliu.commonmain.data.api

import com.rayliu.commonmain.data.dto.NetworkCrawerResult

interface BookSearchService {
    suspend fun postBooks(keyword: String): NetworkCrawerResult
    suspend fun postBooks(stores: List<String>, keyword: String): NetworkCrawerResult
    suspend fun getSearchSnapshot(searchId: String): NetworkCrawerResult
}