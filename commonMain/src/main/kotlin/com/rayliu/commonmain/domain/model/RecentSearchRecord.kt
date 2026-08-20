package com.rayliu.commonmain.domain.model

import kotlinx.datetime.LocalDateTime

data class RecentSearchRecord(
    val query: String,
    val lastSearchedAt: LocalDateTime,
    val times: Int
)
