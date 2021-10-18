package n7.mev

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import n7.mev.ui.heroes.HeroesFragment
import n7.mev.ui.sounds.SoundsFragment

class MainActivity : AppCompatActivity(), Navigator {

    private var shouldKeepOnScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepVisibleCondition(::shouldKeepOnScreen)
        lifecycleScope.launch {
            delay(2000)
            shouldKeepOnScreen = false
        }
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.container, HeroesFragment.newInstance())
            }
        }
    }

    override fun openSoundsFragment(moduleName: String) {
        supportFragmentManager.commit {
            addToBackStack(null)
            add(R.id.container, SoundsFragment.newInstance(moduleName))
        }
    }

}

interface Navigator {

    fun openSoundsFragment(moduleName: String)

}
