@file:UnstableApi

package n7.mev.ui.sounds

import android.app.Application
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import n7.mev.R

class Player(
    private val application: Application
) : Player.Listener {

    val isPlaying = MutableLiveData(false)
    private val exoPlayer = ExoPlayer.Builder(application).build()

    init {
        exoPlayer.addListener(this)
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
            .build()

        exoPlayer.setAudioAttributes(audioAttributes, true)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_IDLE -> Unit
            ExoPlayer.STATE_BUFFERING -> Unit
            ExoPlayer.STATE_READY -> Unit
            ExoPlayer.STATE_ENDED -> isPlaying.value = false
            else -> isPlaying.value = false
        }
    }

    fun play(uri: Uri) {
        isPlaying.value = true
        val source = buildMediaSource(uri)

        exoPlayer.setMediaSource(source)
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
    }

    private fun buildMediaSource(uri: Uri): ProgressiveMediaSource {
        val userAgent = Util.getUserAgent(application, application.getString(R.string.app_name))
        val dataSourceFactory = DefaultDataSourceFactory(application, userAgent)
        val mediaItem = MediaItem.fromUri(uri)
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
    }

}
