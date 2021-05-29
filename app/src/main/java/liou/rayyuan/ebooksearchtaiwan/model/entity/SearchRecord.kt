package liou.rayyuan.ebooksearchtaiwan.model.entity

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime

@Parcelize
@Entity(tableName = "search_records")
data class SearchRecord(
        @NonNull @ColumnInfo(name = "result_text") val resultText: String,
        val counts: Int,
        @ColumnInfo(name = "time_stamp") val timeStamps: OffsetDateTime? = null
        ) : Parcelable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    override fun toString(): String {
        return resultText
    }
}
