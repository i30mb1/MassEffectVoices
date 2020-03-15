package n7.mev.main

import androidx.paging.DataSource

class SoundSourceFactory(private val soundStorage: SoundStorage) : DataSource.Factory<Int, SoundModel>() {
    private var soundDataSource: SoundDataSource? = null
    override fun create(): DataSource<Int, SoundModel> {
        soundDataSource = SoundDataSource(soundStorage)
        return soundDataSource
    }

    init {
        soundStorage.setInvalidate {
            if (soundDataSource != null) {
                soundDataSource!!.invalidate()
            }
        }
    }
}