package n7.mev.main

import android.Manifest
import android.app.AlertDialog
import android.app.Application
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import n7.mev.R
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MainViewModel(application: Application, moduleName: String) : AndroidViewModel(application) {
    val grandSettingEvent = MutableLiveData<Void?>()
    val startActivityForResultSaveFile = MutableLiveData<Intent?>()
    val fileToSaveFile = MutableLiveData<ByteArray?>()
    val grandSPermission = MutableLiveData<Void?>()
    val showSnackBarFolder = MutableLiveData<Void?>()

    @kotlin.jvm.JvmField
    var isLoading = MutableLiveData(true)
    private var lastPlaying = MutableLiveData(false)
    private val diskIO: Executor
    private var mediaPlayer: MediaPlayer? = null

    fun getStartActivityForResultSaveFile(): LiveData<Intent?> {
        return startActivityForResultSaveFile
    }

    val fileSaveFile: LiveData<ByteArray?>
        get() = fileToSaveFile


    private fun canWriteInSystem(): Boolean {
        if (Settings.System.canWrite(getApplication())) {
            return true
        } else {
            grandSettingEvent
            return false
        }
    }

    private fun canWriteExternalStorage(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            grandSPermission
            false
        } else {
            true
        }
    }

    private fun callForShowSnackbarForFolder() {
        showSnackBarFolder
    }

    fun showMenu(context: Context): Boolean {
        val builder = AlertDialog.Builder(context)
        builder.setItems(R.array.hero_responses_menu) { dialog, which ->
            when (which) {
                0 -> if (canWriteExternalStorage()) {
//                    downloadInFolderNew(context, soundModel)
//                    downloadInFolder(context, soundModel)
                    //                            callForShowSnackbarForFolder();
                }
                1 -> if (canWriteExternalStorage() && canWriteInSystem()) {
//                    setAsRingtone(getApplication(), soundModel)
                }
            }
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.window?.attributes?.windowAnimations = R.style.DialogTheme
        dialog.show()
        return true
    }

    fun startPlay(context: Context) {
        try {
            if (lastPlaying != null) {
                lastPlaying!!.value = (false)
            }
//            soundModel.playing!!.value = (true)
            if (mediaPlayer != null) {
                mediaPlayer!!.release()
            }
//            lastPlaying = soundModel.playing
            mediaPlayer = MediaPlayer()
//            val fileDescriptor = context.assets.openFd(soundModel.path!!)
//            mediaPlayer!!.setDataSource(fileDescriptor.fileDescriptor, fileDescriptor.startOffset, fileDescriptor.length)
//            mediaPlayer!!.prepareAsync()
//            mediaPlayer!!.setOnPreparedListener { mediaPlayer!!.start() }
//            mediaPlayer!!.setOnCompletionListener { soundModel.playing!!.value = (false) }
        } catch (e: IOException) {
//            soundModel.playing!!.value = (false)
            e.printStackTrace()
        }
    }

    fun setAsRingtone(context: Context) {
        val assetManager = context.assets
        var outFile: File? = null
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
//            `in` = assetManager.open(soundModel.path!!)
//            val path = File(Environment.getExternalStorageDirectory().path + File.separator + context.getString(R.string.app_name))
//            if (!path.exists()) path.mkdirs()
//            outFile = File(path, "MEV.RINGTONE.ogg")
//            out = FileOutputStream(outFile)
//            copyFile(`in`, out)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `in`?.close()
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val myFile = outFile ?: return
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DATA, myFile.absolutePath)
//        values.put(MediaStore.MediaColumns.TITLE, "MEV." + soundModel.id)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/ogg")
        values.put(MediaStore.MediaColumns.SIZE, myFile.length())
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true)
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true)
        values.put(MediaStore.Audio.Media.IS_ALARM, true)
        values.put(MediaStore.Audio.Media.IS_MUSIC, false)
        val contentResolver = context.contentResolver
        val generalaudiouri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val generalaudiouri2 = MediaStore.Audio.Media.getContentUriForPath(myFile.path)
        contentResolver.delete(generalaudiouri, MediaStore.MediaColumns.DATA + "='" + myFile.absolutePath + "'", null)
        val ringtoneuri = contentResolver.insert(generalaudiouri, values)
        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, ringtoneuri)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(myFile)
            scanIntent.data = contentUri
            getApplication<Application>().sendBroadcast(scanIntent)
        } else {
            val intent = Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()))
            getApplication<Application>().sendBroadcast(intent)
        }
    }

    fun downloadManager(context: Context) {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        val request = DownloadManager.Request(Uri.parse("file:///android_asset/" + soundModel.path))
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        request.setDestinationInExternalPublicDir(
//            Environment.getExternalStorageDirectory().path,
//            File.separator + context.getString(R.string.app_name) + File.separator + soundModel.name
//        )
//        val referese = dm.enqueue(request)
        //        Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
    }

    fun downloadInFolderNew(context: Context) {
        val assetManager = context.assets
        var `in`: InputStream? = null
        val out: OutputStream? = null
        var fileBytes: ByteArray? = null
        try {
//            `in` = assetManager.open(soundModel.path!!)
//            fileBytes = ByteArray(`in`.available())
//            `in`.read(fileBytes)
//            `in`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var intent: Intent? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.type = "audio/ogg"
//            intent.putExtra(Intent.EXTRA_TITLE, soundModel.name)
            if (intent.resolveActivity(context.packageManager) != null) {
                fileToSaveFile.value = fileBytes
                fileToSaveFile.value = null
                startActivityForResultSaveFile.value = intent
                startActivityForResultSaveFile.value = null
            }
        }
    }

    fun downloadInFolder(context: Context) {
        val assetManager = context.assets
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
//            `in` = assetManager.open(soundModel.path!!)
//            val path = File(Environment.getExternalStorageDirectory().path + File.separator + context.getString(R.string.app_name))
//            if (!path.exists()) path.mkdirs()
//            val outFile = File(path, "MEV." + soundModel.hashCode() + ".ogg")
//            out = FileOutputStream(outFile)
//            copyFile(`in`, out)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `in`?.close()
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            //            callForShowSnackbarForFolder();
        }
    }

    private fun copyFile(`in`: InputStream?, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        try {
            while (`in`!!.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    init {
        diskIO = Executors.newSingleThreadExecutor()
//        soundStorage = SoundStorage(application, moduleName, diskIO)
//        soundStorage.load(isLoading)
    }
}