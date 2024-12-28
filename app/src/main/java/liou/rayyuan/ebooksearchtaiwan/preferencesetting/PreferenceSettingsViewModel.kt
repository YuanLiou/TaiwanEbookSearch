package liou.rayyuan.ebooksearchtaiwan.preferencesetting

import androidx.lifecycle.ViewModel
import com.rayliu.commonmain.domain.usecase.DeleteAllSearchRecordUseCase

class PreferenceSettingsViewModel(
    private val deleteAllSearchRecord: DeleteAllSearchRecordUseCase
) : ViewModel() {
    suspend fun deleteAllSearchRecords() {
        deleteAllSearchRecord()
    }
}
