package n7.mev.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.api.load

@BindingAdapter("loadImageByName")
fun ImageView.loadImage(name: String?) {
    val resourceId = resources.getIdentifier(name, "drawable", context.packageName)
    load(resourceId)
}