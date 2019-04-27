package liou.rayyuan.ebooksearchtaiwan.model.entity

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.OffsetDateTime

@Parcelize
@Entity(tableName = "search_records")
data class SearchRecord(
        @NonNull @PrimaryKey(autoGenerate = true) val id: Int,
        @NonNull @ColumnInfo(name = "result_text") val resultText: String,
        val counts: Int,
        @ColumnInfo(name = "time_stamp") val timeStamps: OffsetDateTime? = null
        ) : Parcelable {
    override fun toString(): String {
        return resultText
    }
}
