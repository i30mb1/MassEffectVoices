package n7.mev

inline fun <T> lazyUnsafe(
    crossinline initializer: () -> T
) = lazy(mode = LazyThreadSafetyMode.NONE) {
    initializer.invoke()
}