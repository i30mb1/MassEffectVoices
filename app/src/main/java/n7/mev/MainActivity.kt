package n7.mev

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.play.core.splitcompat.SplitCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import n7.mev.ui.heroes.HeroesRoute
import n7.mev.ui.heroes.HeroesViewModel
import n7.mev.ui.sounds.SoundsRoute
import n7.mev.ui.theme.MevTheme

class MainActivity : ComponentActivity() {

    private var shouldKeepOnScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SplitCompat.install(this)
        installSplashScreen().setKeepOnScreenCondition(::shouldKeepOnScreen)
        lifecycleScope.launch {
            delay(2000)
            shouldKeepOnScreen = false
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            MevTheme {
                MevApp()
            }
        }
    }

}

@Composable
private fun MevApp() {
    val heroesViewModel: HeroesViewModel = viewModel()
    // module of the opened hero, null on the squad screen
    var openedModule by rememberSaveable { mutableStateOf<String?>(null) }

    AnimatedContent(
        targetState = openedModule,
        transitionSpec = {
            if (targetState != null) {
                (slideInHorizontally { it / 3 } + fadeIn()) togetherWith fadeOut()
            } else {
                fadeIn() togetherWith (slideOutHorizontally { it / 3 } + fadeOut())
            }
        },
        label = "screen",
    ) { moduleName ->
        if (moduleName == null) {
            HeroesRoute(
                viewModel = heroesViewModel,
                onHeroClicked = { hero -> openedModule = hero.moduleName },
            )
        } else {
            BackHandler { openedModule = null }
            SoundsRoute(moduleName = moduleName, onBack = { openedModule = null })
        }
    }
}
