package n7.mev.main

import androidx.paging.DataSource

class SoundSourceFactory(private val soundStorage: SoundStorage) : DataSource.Factory<Int, SoundModel>() {
    private lateinit var soundDataSource: SoundDataSource

    override fun create(): DataSource<Int, SoundModel> {
        soundDataSource = SoundDataSource(soundStorage)
        return soundDataSource
    }

    init {
        soundStorage.setListener(object : Invalidate {
            override fun invalidate() {
                soundDataSource.invalidate()
            }
        })
    }
}
