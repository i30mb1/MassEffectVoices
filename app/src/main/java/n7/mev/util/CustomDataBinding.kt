package n7.mev.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("loadImageByName")
fun ImageView.loadImage(name: String?) {
    val resourceId = resources.getIdentifier(name, "drawable", context.packageName)
    Picasso.get().load(resourceId).into(this)
}