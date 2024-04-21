package com.rayliu.commonmain

import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

class OffsetDateTypeConverter {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let {
            return formatter.parse(it, OffsetDateTime::from)
        }
    }

    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.let {
            return it.format(formatter)
        }
    }
}
