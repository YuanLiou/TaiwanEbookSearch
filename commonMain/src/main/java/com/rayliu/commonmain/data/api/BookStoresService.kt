package com.rayliu.commonmain.data.api

import com.rayliu.commonmain.data.dto.NetworkBookStore

interface BookStoresService {
    suspend fun getBookStores(): List<NetworkBookStore>
}
