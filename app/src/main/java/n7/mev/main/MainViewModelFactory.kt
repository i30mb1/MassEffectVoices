package n7.mev.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory

class MainViewModelFactory(private val application: Application, private val moduleName: String) : NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == MainViewModel::class.java) {
            MainViewModel(application, moduleName) as T
        } else super.create(modelClass)
    }

}