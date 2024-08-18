package liou.rayyuan.ebooksearchtaiwan.ui.composables

sealed class DeviceOrientation {
    abstract val orientation: Int

    data class Portrait(
        override val orientation: Int
    ) : DeviceOrientation()

    data class ReverseLandscape(
        override val orientation: Int
    ) : DeviceOrientation()

    data class Landscape(
        override val orientation: Int
    ) : DeviceOrientation()
}
