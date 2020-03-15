package n7.mev.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import n7.mev.R
import n7.mev.modules.ModulesFragment

class MainActivity : AppCompatActivity() {
    private val mViewModel: MainViewModel by viewModels {
        MainViewModelFactory(application, intent.getStringExtra(ModulesFragment.MODULE_NAME))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        //        hideSystemUI();
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance()).commitNow()
        }
    }
}