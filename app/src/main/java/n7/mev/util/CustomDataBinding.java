package n7.mev.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

import androidx.databinding.BindingAdapter;

public class CustomDataBinding {

    @BindingAdapter("animateWhenLoading")
    public static void animateWhenLoading(View view, boolean isLoading) {
        Animation animation = view.getAnimation();
        if (isLoading && animation == null) {
            view.startAnimation(getAnimation());
        } else if (animation != null) {
            animation.cancel();
            view.setAnimation(null);
        }
    }

    private static Animation getAnimation() {
        RotateAnimation anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(1000);
        anim.setRepeatCount(TranslateAnimation.INFINITE);
        return anim;
    }

}
