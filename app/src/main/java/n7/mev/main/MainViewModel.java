package n7.mev.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import n7.mev.R;

import static android.content.Context.DOWNLOAD_SERVICE;
import static n7.mev.main.MainFragment.REQUESTED_PERMISSION;

public class MainViewModel extends AndroidViewModel {

    private final SingleLiveEvent<Void> grandSettingEvent = new SingleLiveEvent<>();
    private final MutableLiveData<Intent> startActivityForResultSaveFile = new MutableLiveData<>();
    private final MutableLiveData<byte[]> fileToSaveFile = new MutableLiveData<>();
    private final SingleLiveEvent<Void> grandSPermission = new SingleLiveEvent<>();
    private final SingleLiveEvent<Void> showSnackBarFolder = new SingleLiveEvent<>();
    public ObservableBoolean isLoading = new ObservableBoolean(true);
    private ObservableBoolean lastPlaying;
    private SoundStorage soundStorage;
    private Application application;
    private Executor diskIO;
    private MediaPlayer mediaPlayer;

    public MainViewModel(@NonNull final Application application, String moduleName) {
        super(application);
        this.application = application;
        this.diskIO = Executors.newSingleThreadExecutor();

        soundStorage = new SoundStorage(application, moduleName, diskIO);
        soundStorage.load(isLoading);
    }

    public LiveData<PagedList<SoundModel>> createPagedListData(int lastVisibleItem) {
        SoundSourceFactory soundSourceFactory = new SoundSourceFactory(soundStorage);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(100)
                .build();

        LiveData<PagedList<SoundModel>> pagedList = new LivePagedListBuilder<>(soundSourceFactory, config)
                .setInitialLoadKey(lastVisibleItem)
                .build();

        return pagedList;
    }

    public SingleLiveEvent<Void> getShowSnackBarFolder() {
        return showSnackBarFolder;
    }

    public LiveData<Intent> getStartActivityForResultSaveFile() {
        return startActivityForResultSaveFile;
    }
    public LiveData<byte[]> getFileSaveFile() {
        return fileToSaveFile;
    }

    public SingleLiveEvent<Void> getGrandSPermission() {
        return grandSPermission;
    }

    void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTED_PERMISSION && resultCode == Activity.RESULT_OK) {

        }
    }

    private boolean canWriteInSystem() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(application)) {
                return true;
            } else {
                grandSettingEvent.call();
                return false;
            }
        } else return true;
    }

    private boolean canWriteExternalStorage() {
        if (ContextCompat.checkSelfPermission(application, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            grandSPermission.call();
            return false;
        } else {
            return true;
        }
    }

    private void callForShowSnackbarForFolder() {
        showSnackBarFolder.call();
    }

    public boolean showMenu(final Context context, final SoundModel soundModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.hero_responses_menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (canWriteExternalStorage()) {
                            downloadInFolderNew(context, soundModel);
                            downloadInFolder(context,soundModel);
//                            callForShowSnackbarForFolder();
                        }
                        break;
                    case 1:
                        if (canWriteExternalStorage() && canWriteInSystem()) {
                            setAsRingtone(application, soundModel);
                        }
                        break;
                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        dialog.show();
        return true;
    }

    public SingleLiveEvent<Void> getGrandSettingEvent() {
        return grandSettingEvent;
    }

    public void startPlay(Context context, final SoundModel soundModel) {
        try {
            if (lastPlaying != null) {
                lastPlaying.set(false);
            }
            soundModel.playing.set(true);
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            lastPlaying = soundModel.playing;

            mediaPlayer = new MediaPlayer();
            AssetFileDescriptor fileDescriptor = context.getAssets().openFd(soundModel.getPath());
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    soundModel.playing.set(false);
                }
            });

        } catch (IOException e) {
            soundModel.playing.set(false);
            e.printStackTrace();
        }

    }

    public void setAsRingtone(Context context, SoundModel soundModel) {
        AssetManager assetManager = context.getAssets();
        File outFile = null;
        InputStream in = null;
        OutputStream out = null;

        try {
            in = assetManager.open(soundModel.getPath());
            File path = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + context.getString(R.string.app_name));
            if (!path.exists()) path.mkdirs();

            outFile = new File(path, "MEV.RINGTONE.ogg");
            out = new FileOutputStream(outFile);
            copyFile(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File myFile = outFile;
        if (myFile == null) return;
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, myFile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, "MEV." + soundModel.getId());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/ogg");
        values.put(MediaStore.MediaColumns.SIZE, myFile.length());
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        ContentResolver contentResolver = context.getContentResolver();
        Uri generalaudiouri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri generalaudiouri2 = MediaStore.Audio.Media.getContentUriForPath(myFile.getPath());
        contentResolver.delete(generalaudiouri, MediaStore.MediaColumns.DATA + "='" + myFile.getAbsolutePath() + "'", null);
        Uri ringtoneuri = contentResolver.insert(generalaudiouri, values);
        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, ringtoneuri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            final Uri contentUri = Uri.fromFile(myFile);
            scanIntent.setData(contentUri);
            application.sendBroadcast(scanIntent);
        } else {
            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
            application.sendBroadcast(intent);
        }
    }

    public void downloadManager(Context context, SoundModel soundModel) {
        DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("file:///android_asset/" + soundModel.getPath()));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory().getPath(), File.separator + context.getString(R.string.app_name) + File.separator + soundModel.getName());
        Long referese = dm.enqueue(request);
//        Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
    }

    public void downloadInFolderNew(Context context, SoundModel soundModel) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        byte[] fileBytes = null;

        try {
            in = assetManager.open(soundModel.getPath());
            fileBytes=new byte[in.available()];
            in.read(fileBytes);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.setType("audio/ogg");
            intent.putExtra(Intent.EXTRA_TITLE, soundModel.getName());
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                fileToSaveFile.setValue(fileBytes);
                fileToSaveFile.setValue(null);
                startActivityForResultSaveFile.setValue(intent);
                startActivityForResultSaveFile.setValue(null);
            }
        }

    }

    public void downloadInFolder(Context context, SoundModel soundModel) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;

        try {
            in = assetManager.open(soundModel.getPath());
            File path = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + context.getString(R.string.app_name));
            if (!path.exists()) path.mkdirs();

            File outFile = new File(path, "MEV." + soundModel.hashCode() + ".ogg");
            out = new FileOutputStream(outFile);
            copyFile(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
//            callForShowSnackbarForFolder();
        }
    }

    private void copyFile(InputStream in, OutputStream out) {
        byte[] buffer = new byte[1024];
        int read;
        try {
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
