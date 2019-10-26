package n7.mev.main;

import android.app.Application;
import android.util.Pair;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Executor;

import androidx.databinding.ObservableBoolean;

public class SoundStorage {

    private String[] currentSounds = new String[]{};
    private String moduleName;
    public Invalidate invalidate;
    private Executor executor;
    private Application application;

    public SoundStorage(Application application, String moduleName, Executor executor) {
        this.application = application;
        this.moduleName = moduleName;
        this.executor = executor;
    }

    public void load(final ObservableBoolean isLoading) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                try {
                    currentSounds = application.getAssets().list(moduleName);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (invalidate != null) invalidate.invalidate();
                    if (isLoading != null) isLoading.set(false);
                }
            }
        });
    }

    public int getSize() {
        if (currentSounds != null) {
            return currentSounds.length;
        } else {
            return 0;
        }
    }

    public Pair<LinkedList<SoundModel>, Integer> getData(int startPosition, int size) {
        LinkedList<SoundModel> sounds = new LinkedList<>();
        int endPosition = startPosition + size;
        for (int i = startPosition; i < endPosition; i++) {
            if (i >= 0 && i < currentSounds.length) {
                SoundModel model = new SoundModel(currentSounds[i], moduleName, i);
                sounds.add(model);
            }
        }

        if (startPosition > currentSounds.length) startPosition = 0;
        return new Pair<>(sounds, startPosition);
    }

    public void setInvalidate(Invalidate invalidate) {
        this.invalidate = invalidate;
    }

}
