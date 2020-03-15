package n7.mev.main

import android.app.Application
import android.util.Pair
import androidx.databinding.ObservableBoolean
import java.io.IOException
import java.util.*
import java.util.concurrent.Executor

class SoundStorage(private val application: Application, private val moduleName: String?, private val executor: Executor) {
    private var currentSounds: Array<String>? = arrayOf()
    var invalidate: Invalidate? = null
    fun load(isLoading: ObservableBoolean?) {
        executor.execute { //                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            try {
                currentSounds = application.assets.list(moduleName)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (invalidate != null) invalidate!!.invalidate()
                isLoading?.set(false)
            }
        }
    }

    val size: Int
        get() = if (currentSounds != null) {
            currentSounds!!.size
        } else {
            0
        }

    fun getData(startPosition: Int, size: Int): Pair<LinkedList<SoundModel>, Int> {
        var startPosition = startPosition
        val sounds = LinkedList<SoundModel>()
        val endPosition = startPosition + size
        for (i in startPosition until endPosition) {
            if (i >= 0 && i < currentSounds!!.size) {
                val model = SoundModel(currentSounds!![i], moduleName, i)
                sounds.add(model)
            }
        }
        if (startPosition > currentSounds!!.size) startPosition = 0
        return Pair(sounds, startPosition)
    }

    fun setListener(invalidate: Invalidate) {
        this.invalidate = invalidate
    }

}