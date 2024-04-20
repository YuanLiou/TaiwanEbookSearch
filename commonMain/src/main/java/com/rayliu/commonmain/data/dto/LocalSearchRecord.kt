package com.rayliu.commonmain.data.dto

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.IgnoredOnParcel
import org.threeten.bp.OffsetDateTime

@Parcelize
@Entity(tableName = "search_records")
data class LocalSearchRecord(
    @ColumnInfo(name = "result_text") val resultText: String,
    val counts: Int,
    @ColumnInfo(name = "time_stamp") val timeStamps: OffsetDateTime? = null
) : Parcelable {
    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    override fun toString(): String = resultText
}
