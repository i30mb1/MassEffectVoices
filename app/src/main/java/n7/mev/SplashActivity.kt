package n7.mev

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import n7.mev.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    companion object {
        const val DELAY_START_ACTIVITY = 1000L
        const val DELAY_FADE_ANIMATION = 600L
        const val FADE_DURATION_ANIMATION = 1000L
    }

    private var fadeIn = false
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        finish()
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        setTheme(R.style.SplashTheme)
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            fadeIn = true
        }

        binding.tvLogo.text = getErrorEmote()
        binding.ivLogo.setOnClickListener {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        if (fadeIn) {
            // Note: normally should use window animations for this, but there's a bug
            // on Samsung devices where the wallpaper is animated along with the window for
            // windows showing the wallpaper (the wallpaper _should_ be static, not part of
            // the animation).
            window.decorView.run {
                alpha = 0f
                animate().cancel()
                animate().setStartDelay(DELAY_FADE_ANIMATION).withEndAction { startAnimateText() }.alpha(1f).duration = FADE_DURATION_ANIMATION
            }
            fadeIn = false
        }
    }

    private fun startAnimateText() {
        lifecycleScope.launch {
            TransitionManager.beginDelayedTransition(binding.root as ViewGroup)
            binding.tvLogo.visibility = View.VISIBLE
            delay(DELAY_START_ACTIVITY)
            finish()
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        }

    }

}

fun getErrorEmote(): String {
    val emotes = arrayOf(
        "('.')",
        "('x')",
        "(>_<)",
        "(>.<)",
        "(;-;)",
        "\\(o_o)/",
        "(O_o)",
        "(o_0)",
        "(≥o≤)",
            "(≥o≤)",
            "(·.·)",
            "(·_·)"
    )
    return emotes.random()
}