package liou.rayyuan.ebooksearchtaiwan.arch

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.MutableSharedFlow

interface IModel<S : IState, I : IUserIntent> {
    val userIntents: MutableSharedFlow<I>
    val viewState: LiveData<S>
}
