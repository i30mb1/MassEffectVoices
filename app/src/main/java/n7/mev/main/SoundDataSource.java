package n7.mev.main;

import android.util.Pair;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

public class SoundDataSource extends PositionalDataSource<SoundModel> {

    private final SoundStorage soundStorage;

    public SoundDataSource(SoundStorage soundStorage) {
        this.soundStorage = soundStorage;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<SoundModel> callback) {
        int initialKey = params.requestedStartPosition;
        Pair<LinkedList<SoundModel>, Integer> pair = soundStorage.getData(initialKey, params.requestedLoadSize);
        int totalCount = soundStorage.getSize();
        if (params.placeholdersEnabled) {
            callback.onResult(pair.first, pair.second, totalCount);
        } else {
            callback.onResult(pair.first, pair.second);
        }
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<SoundModel> callback) {
        Pair<LinkedList<SoundModel>, Integer> pair = soundStorage.getData(params.startPosition, params.loadSize);
        callback.onResult(pair.first);
    }
}
