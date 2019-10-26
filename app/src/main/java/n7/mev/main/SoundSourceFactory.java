package n7.mev.main;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;

public class SoundSourceFactory extends DataSource.Factory<Integer, SoundModel> {

    private SoundStorage soundStorage;
    private SoundDataSource soundDataSource;

    public SoundSourceFactory(SoundStorage soundStorage) {
        this.soundStorage = soundStorage;
        soundStorage.setInvalidate(new Invalidate() {
            @Override
            public void invalidate() {
                if (soundDataSource != null) {
                    soundDataSource.invalidate();
                }
            }
        });
    }

    @NonNull
    @Override
    public DataSource<Integer, SoundModel> create() {
        soundDataSource = new SoundDataSource(soundStorage);
        return soundDataSource;
    }

}
