package com.rayliu.commonmain.data.mapper

import com.rayliu.commonmain.data.dto.LocalSearchRecord
import com.rayliu.commonmain.data.mapper.basic.Mapper
import com.rayliu.commonmain.domain.model.SearchRecord

class SearchRecordMapper : Mapper<LocalSearchRecord, SearchRecord> {
    override fun map(input: LocalSearchRecord): SearchRecord = SearchRecord(input.id?.toInt(), input.counts.toInt(), input.resultText)
}
