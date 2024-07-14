package liou.rayyuan.ebooksearchtaiwan.preferencesetting

import androidx.lifecycle.ViewModel
import com.rayliu.commonmain.domain.usecase.DeleteAllSearchRecordUseCase
import liou.rayyuan.ebooksearchtaiwan.utils.QuickChecker

class PreferenceSettingsViewModel(
    private val quickChecker: QuickChecker,
    private val deleteAllSearchRecord: DeleteAllSearchRecordUseCase,
) : ViewModel() {
    val isTabletSize: Boolean
        get() = quickChecker.isTabletSize()

    suspend fun deleteAllSearchRecords() {
        deleteAllSearchRecord()
    }
}
