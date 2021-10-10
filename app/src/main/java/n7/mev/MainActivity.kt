package n7.mev

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import n7.mev.ui.heroes.HeroesFragment
import n7.mev.ui.sounds.SoundsFragment

class MainActivity : AppCompatActivity(), Navigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
