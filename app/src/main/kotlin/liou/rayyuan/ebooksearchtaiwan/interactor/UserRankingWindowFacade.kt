package liou.rayyuan.ebooksearchtaiwan.interactor

import com.rayliu.commonmain.domain.usecase.GetIsUserSeenRankWindowUseCase
import com.rayliu.commonmain.domain.usecase.SaveUserHasSeenRankWindowUseCase

class UserRankingWindowFacade(
    val isUserSeenRankWindow: GetIsUserSeenRankWindowUseCase,
    val saveUserHasSeenRankWindow: SaveUserHasSeenRankWindowUseCase
)
