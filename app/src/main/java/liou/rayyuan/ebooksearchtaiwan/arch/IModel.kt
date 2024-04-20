package liou.rayyuan.ebooksearchtaiwan.arch

import androidx.lifecycle.LiveData
import kotlinx.coroutines.channels.Channel

interface IModel<S : IState, I : IUserIntent> {
    val userIntents: Channel<I>
    val viewState: LiveData<S>
}
