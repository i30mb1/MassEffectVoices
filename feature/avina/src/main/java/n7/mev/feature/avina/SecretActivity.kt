package n7.mev.feature.avina

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.play.core.splitcompat.SplitCompat

class SecretActivity : FragmentActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.installActivity(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(n7.mev.R.layout.activity_secret)
    }
}
