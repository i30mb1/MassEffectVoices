package n7.mev.ui.heroes.vo

import androidx.annotation.DrawableRes

data class HeroVO(
    val name: String,
    @DrawableRes val icon: Int,
    val featureName: String
)