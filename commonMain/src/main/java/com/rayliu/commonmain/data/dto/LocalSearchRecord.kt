package com.rayliu.commonmain.data.dto

import org.threeten.bp.OffsetDateTime

data class LocalSearchRecord(
    val resultText: String,
    val counts: Long,
    val timeStamps: OffsetDateTime? = null
) {
    var id: Long? = null

    override fun toString(): String = resultText
}
