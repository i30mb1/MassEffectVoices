package n7.mev.main

import androidx.lifecycle.MutableLiveData
import java.io.File

class SoundModel(name: String, hero: String?, id: Int) {
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as SoundModel
        if (id != that.id) return false
        if (if (playing != null) playing != that.playing else that.playing != null) return false
        if (if (name != null) name != that.name else that.name != null) return false
        return if (path != null) path == that.path else that.path == null
    }

    override fun hashCode(): Int {
        var result = if (playing != null) playing.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        result = 31 * result + (path?.hashCode() ?: 0)
        result = 31 * result + id
        return result
    }

    @kotlin.jvm.JvmField
    var playing = MutableLiveData(false)
    var name: String?
    val path: String?
    val id: Int

    init {
        path = hero + File.separator + name
        this.id = id
        this.name = name
    }
}