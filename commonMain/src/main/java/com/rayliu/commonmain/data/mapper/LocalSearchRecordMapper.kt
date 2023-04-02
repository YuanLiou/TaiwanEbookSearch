package com.rayliu.commonmain.data.mapper

import com.rayliu.commonmain.data.dto.LocalSearchRecord
import com.rayliu.commonmain.data.mapper.basic.Mapper
import com.rayliu.commonmain.domain.model.SearchRecord

class LocalSearchRecordMapper : Mapper<SearchRecord, LocalSearchRecord> {
    override fun map(input: SearchRecord): LocalSearchRecord {
        return LocalSearchRecord(
            input.text,
            input.times,
            null
        ).also {
            if (input.id != 0) {
                it.id = input.id
            }
        }
    }
}