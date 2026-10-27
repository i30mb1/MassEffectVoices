package n7.mev.ui.common

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.mev.appGraph
import java.util.concurrent.ConcurrentHashMap

/**
 * Hero portraits decoded once, off the main thread. `painterResource` would decode the PNG on the main
 * thread every time a card enters composition, upscaled to the screen density.
 */
class PortraitCache(
    private val resources: Resources,
    private val dispatcher: CoroutineDispatcher,
) {

    private val cache = ConcurrentHashMap<Int, ImageBitmap>()
    private val options = BitmapFactory.Options().apply { inScaled = false }

    fun peek(@DrawableRes id: Int): ImageBitmap? = cache[id]

    suspend fun load(@DrawableRes id: Int): ImageBitmap? = cache[id] ?: withContext(dispatcher) {
        BitmapFactory.decodeResource(resources, id, options)
            ?.apply { prepareToDraw() }
            ?.asImageBitmap()
            ?.also { cache[id] = it }
    }

}

/** Null while the portrait is being decoded the first time, or when [id] is 0. */
@Composable
fun rememberPortrait(@DrawableRes id: Int): ImageBitmap? {
    if (id == 0) return null
    val cache = LocalContext.current.appGraph.portraitCache
    val portrait by produceState(initialValue = cache.peek(id), id) {
        if (value == null) value = cache.load(id)
    }
    return portrait
}
