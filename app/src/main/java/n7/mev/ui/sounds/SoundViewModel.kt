package n7.mev.ui.sounds

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
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import n7.mev.R
import n7.mev.data.source.local.SoundRepository
import n7.mev.ui.sounds.usecase.GetSoundsVOUseCase
import n7.mev.ui.sounds.vo.SoundVO
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class SoundViewModel(
    private val applicationContext: Application
) : AndroidViewModel(applicationContext) {

    private val _state: MutableLiveData<State> = MutableLiveData(State.Loading)
    val state: LiveData<State> = _state

    private val soundRepository = SoundRepository(applicationContext)
    private val getSoundsVOUseCase = GetSoundsVOUseCase(applicationContext, soundRepository, Dispatchers.IO)
    private val player = Player(applicationContext)

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

    fun play(url: Uri) {
        player.play(url)
    }

    private fun canWriteInSystem(): Boolean {
        if (Settings.System.canWrite(applicationContext)) {
            return true
        } else {
            grandSettingEvent
            return false
        }
    }

    private fun canWriteExternalStorage(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                applicationContext,
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
//                    setAsRingtone(application(), soundModel)
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
            this.applicationContext.sendBroadcast(scanIntent)
        } else {
            val intent = Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()))
            this.applicationContext.sendBroadcast(intent)
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
        //        Toast.makeText(applicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
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

    fun load(moduleName: String) = viewModelScope.launch {
        _state.value = State.Data(getSoundsVOUseCase(moduleName).first())
    }

    init {
        diskIO = Executors.newSingleThreadExecutor()
//        soundStorage = SoundStorage(application, moduleName, diskIO)
//        soundStorage.load(isLoading)
    }

    companion object {
        sealed class State {
            object Loading : State()
            data class Data(val list: List<SoundVO>) : State()
        }
    }

}