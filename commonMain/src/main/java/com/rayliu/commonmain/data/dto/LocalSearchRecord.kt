package com.rayliu.commonmain.data.dto

import kotlinx.datetime.LocalDateTime

data class LocalSearchRecord(
    val resultText: String,
    val counts: Long,
    val timeStamps: LocalDateTime? = null
) {
    var id: Long? = null

    override fun toString(): String = resultText
}
