package n7.mev.main

import androidx.paging.PositionalDataSource

class SoundDataSource(private val soundStorage: SoundStorage) : PositionalDataSource<SoundModel>() {

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<SoundModel>) {
        val initialKey = params.requestedStartPosition
        val pair = soundStorage.getData(initialKey, params.requestedLoadSize)
        val totalCount = soundStorage.size
        if (params.placeholdersEnabled) {
            callback.onResult(pair.first as List<SoundModel>, pair.second, totalCount)
        } else {
            callback.onResult(pair.first as List<SoundModel>, pair.second)
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<SoundModel?>) {
        val pair = soundStorage.getData(params.startPosition, params.loadSize)
        callback.onResult(pair.first as List<SoundModel?>)
    }

}