package n7.mev.data.source.local

import android.app.Application
import java.util.concurrent.ConcurrentHashMap

enum class MePart { ME1, ME2, ME3 }

/** Reads sounds from the assets of installed feature modules: `<module>/<MePart>/[folder/]<file>.ogg`. */
class SoundRepository(
    private val application: Application,
) {

    data class Sound(val name: String, val folder: String, val path: String) {
        val url: String get() = "asset:///$path"
    }

    // Module assets never change while the process lives, and AssetManager.list is slow
    private val listCache = ConcurrentHashMap<String, List<String>>()

    fun getMEParts(module: String): List<MePart> {
        return list(module)
            .mapNotNull { name -> MePart.entries.firstOrNull { it.name == name } }
            .sorted()
    }

    /** Sounds of one part grouped by folder; files lying directly in the part folder are grouped under [MePart.name]. */
    fun getSounds(module: String, mePart: MePart): Map<String, List<Sound>> {
        val partPath = "$module/$mePart"
        val result = linkedMapOf<String, MutableList<Sound>>()
        for (entry in list(partPath).sorted()) {
            if (entry.isSoundFile()) {
                result.getOrPut(mePart.name) { mutableListOf() } += Sound(entry, mePart.name, "$partPath/$entry")
            } else {
                val sounds = application.assets.list("$partPath/$entry").orEmpty()
                    .filter { it.isSoundFile() }
                    .sorted()
                    .map { name -> Sound(name, entry, "$partPath/$entry/$name") }
                if (sounds.isNotEmpty()) result.getOrPut(entry) { mutableListOf() } += sounds
            }
        }
        return result
    }

    private fun list(path: String): List<String> {
        listCache[path]?.let { return it }
        val entries = application.assets.list(path).orEmpty().toList()
        // an empty listing means the module is not installed yet: do not cache it
        if (entries.isNotEmpty()) listCache[path] = entries
        return entries
    }

    private fun String.isSoundFile() = endsWith(".ogg", ignoreCase = true)

}
