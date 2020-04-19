package n7.mev.util

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("loadImageByName")
fun ImageView.loadImage(name: String?) {
    val resourceId = resources.getIdentifier(name, "drawable", context.packageName)
    Picasso.get().load(resourceId).into(this)
}

@BindingAdapter("isVisible")
fun View.isVisible(isVisible: Boolean) {
    this.visibility = if(isVisible) View.VISIBLE else View.GONE
}