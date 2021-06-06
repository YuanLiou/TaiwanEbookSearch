import java.text.SimpleDateFormat
import java.util.*

fun getVersionCodeTimeStamps(): Int {
    val date = Date()
    val dateFormatter = SimpleDateFormat("yyMMddHH")
    val formattedDate = "19" + dateFormatter.format(date)
    return formattedDate.toInt()
}
