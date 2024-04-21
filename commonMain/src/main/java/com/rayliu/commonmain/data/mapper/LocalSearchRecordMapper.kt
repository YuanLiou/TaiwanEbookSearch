package com.rayliu.commonmain.data.mapper

import com.rayliu.commonmain.data.dto.LocalSearchRecord
import com.rayliu.commonmain.data.mapper.basic.Mapper
import com.rayliu.commonmain.domain.model.SearchRecord
import org.threeten.bp.OffsetDateTime

class LocalSearchRecordMapper : Mapper<SearchRecord, LocalSearchRecord> {
    override fun map(input: SearchRecord): LocalSearchRecord =
        LocalSearchRecord(
            input.text,
            input.times.toLong(),
            OffsetDateTime.now()
        ).also {
            val id = input.id
            if (id != null && id != 0) {
                it.id = id.toLong()
            }
        }
}
