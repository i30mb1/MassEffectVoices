package n7.mev.avina

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.play.core.splitcompat.SplitCompat
import n7.mev.feature_avina.R

class SecretActivity : FragmentActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.installActivity(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secret)
    }
}