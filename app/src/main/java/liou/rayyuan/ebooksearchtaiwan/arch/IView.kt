package liou.rayyuan.ebooksearchtaiwan.arch

/**
 * View relate to specific viewState, handing rendering
 */
interface IView<S : IState> {
    fun render(viewState: S)
}
