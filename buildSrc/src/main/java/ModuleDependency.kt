import kotlin.reflect.full.memberProperties

const val FEATURE_PREFIX = ":feature_"


object ModuleDependency {
    const val APP = ":app"
    const val FEATURE_AVINA        = ":feature_avina"
    const val FEATURE_EDI          = ":feature_edi"
    const val FEATURE_GARRUS       = ":feature_garrus"
    const val FEATURE_GRUNT        = ":feature_grunt"
    const val FEATURE_ILLUSIVE_MAN = ":feature_illusive_man"
    const val FEATURE_JACK         = ":feature_jack"
    const val FEATURE_JACOB        = ":feature_jacob"
    const val FEATURE_JAVIK        = ":feature_javik"
    const val FEATURE_JOKER        = ":feature_joker"
    const val FEATURE_KASUMI       = ":feature_kasumi"
    const val FEATURE_LEGION       = ":feature_legion"
    const val FEATURE_LIARA        = ":feature_liara"
    const val FEATURE_MIRANDA      = ":feature_miranda"
    const val FEATURE_MORDIN       = ":feature_mordin"
    const val FEATURE_REAPER       = ":feature_reaper"
    const val FEATURE_SAMARA       = ":feature_samara"
    const val FEATURE_TALI         = ":feature_tali"
    const val FEATURE_THANE        = ":feature_thane"
    const val FEATURE_VEGA         = ":feature_vega"
    const val FEATURE_ZAEED        = ":feature_zaeed"

    fun getAllModules() = ModuleDependency::class.memberProperties
            .filter { it.isConst }
            .map { it.getter.call().toString() }
            .toSet()

    fun getDynamicFeatureModules() = getAllModules()
            .filter { it.startsWith(FEATURE_PREFIX) }
            .toSet()

}