package n7.mev.util

import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.TranslateAnimation
import androidx.databinding.BindingAdapter

object CustomDataBinding {
    @kotlin.jvm.JvmStatic
    @BindingAdapter("animateWhenLoading")
    fun animateWhenLoading(view: View?, isLoading: Boolean) {
//        if(view==null) return
//        val animation = view.animation
//        if (isLoading && animation == null) {
//            view.startAnimation(animation)
//        } else if (animation != null) {
//            animation.cancel()
//            view.animation = null
//        }
    }

    private val animation: Animation
        private get() {
            val anim = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            anim.duration = 1000
            anim.repeatCount = TranslateAnimation.INFINITE
            return anim
        }
}