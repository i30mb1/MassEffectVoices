package n7.mev.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import n7.mev.R
import n7.mev.modules.ModulesFragment
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var mViewModel: MainViewModel? = null
    lateinit var file: ByteArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        //        hideSystemUI();
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container, MainFragment.Companion.newInstance()).commitNow()
        }
        mViewModel = ViewModelProviders.of(this, MainViewModelFactory(application, intent.getStringExtra(ModulesFragment.Companion.MODULE_NAME))).get(MainViewModel::class.java)
        mViewModel!!.startActivityForResultSaveFile.observe(this, Observer { intent -> intent?.let { startActivityForResult(it, WRITE_STORAGE_CODE) } })
        mViewModel.getFileSaveFile().observe(this, Observer { bytes ->
            if (bytes != null) {
                file = bytes
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            WRITE_STORAGE_CODE -> if (data != null) {
                if (data.data != null) {
                    writeInCreatedFile(data.data)
                }
            }
            else -> {
            }
        }
    }

    private fun writeInCreatedFile(uri: Uri) {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "w")
            if (parcelFileDescriptor != null) {
                val fileOutputStream = FileOutputStream(parcelFileDescriptor.fileDescriptor)
                fileOutputStream.write(file)
            }
            val snack = Snackbar.make(findViewById(R.id.container), "SAVED", Snackbar.LENGTH_LONG)
            snack.setAction("OPEN") {
                val intent = Intent(Intent.ACTION_GET_CONTENT).setType("*/*")
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                    snack.dismiss()
                }
            }
            snack.show()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            val snack = Snackbar.make(findViewById(R.id.container), e.message!!, Snackbar.LENGTH_LONG)
            snack.show()
        } catch (e: IOException) {
            e.printStackTrace()
            val snack = Snackbar.make(findViewById(R.id.container), e.message!!, Snackbar.LENGTH_LONG)
            snack.show()
        }
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    companion object {
        const val WRITE_STORAGE_CODE = 12
    }
}