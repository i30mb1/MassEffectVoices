package n7.mev.ui.sounds

import android.app.Application
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class Player(
    private val application: Application,
    private val scope: CoroutineScope,
) : Player.Listener {

    /** Uri of the sound being played, null when nothing plays. */
    private val _playing = MutableStateFlow<Uri?>(null)
    val playing: StateFlow<Uri?> = _playing.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    // built on the first play: building ExoPlayer costs ~13 ms of the main thread
    private val exoPlayer by lazy {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
            .build()
        ExoPlayer.Builder(application).build().apply {
            addListener(this@Player)
            setAudioAttributes(audioAttributes, true)
        }
    }
    private var progressJob: Job? = null

    override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == Player.STATE_ENDED) stop()
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        progressJob?.cancel()
        if (isPlaying) progressJob = scope.launch {
            while (isActive) {
                val duration = exoPlayer.duration
                if (duration > 0) _progress.value = exoPlayer.currentPosition.toFloat() / duration
                delay(50)
            }
        }
    }

    fun play(uri: Uri) {
        _playing.value = uri
        _progress.value = 0f
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun stop() {
        if (_playing.value == null) return
        exoPlayer.stop()
        _playing.value = null
        _progress.value = 0f
    }

}
