import kotlin.reflect.full.memberProperties

const val FEATURE_PREFIX = ":feature"


object ModuleDependency {
    const val APP = ":app"
    const val FEATURE_AVINA = ":feature:avina"
    const val FEATURE_EDI = ":feature:edi"
    const val FEATURE_GARRUS = ":feature:garrus"
    const val FEATURE_GRUNT = ":feature:grunt"
    const val FEATURE_ILLUSIVE_MAN = ":feature:illusive_man"
    const val FEATURE_JACK = ":feature:jack"
    const val FEATURE_JACOB = ":feature:jacob"
    const val FEATURE_JAVIK = ":feature:javik"
    const val FEATURE_JOKER = ":feature:joker"
    const val FEATURE_KASUMI = ":feature:kasumi"
    const val FEATURE_LEGION = ":feature:legion"
    const val FEATURE_LIARA = ":feature:liara"
    const val FEATURE_MIRANDA = ":feature:miranda"
    const val FEATURE_MORDIN = ":feature:mordin"
    const val FEATURE_REAPER = ":feature:reaper"
    const val FEATURE_SAMARA = ":feature:samara"
    const val FEATURE_TALI = ":feature:tali"
    const val FEATURE_THANE = ":feature:thane"
    const val FEATURE_VEGA = ":feature:vega"
    const val FEATURE_ZAEED = ":feature:zaeed"
    const val FEATURE_ANDERSON = ":feature:anderson"
    const val FEATURE_KAIDAN = ":feature:kaidan"
    const val FEATURE_ASHLEY = ":feature:ashley"
    const val FEATURE_RACHNI_QUEEN = ":feature:rachni_queen"

    fun getAllModules() = ModuleDependency::class.memberProperties
        .filter { it.isConst }
        .map { it.getter.call().toString() }
        .toSet()

    fun getDynamicFeatureModules() = getAllModules()
        .filter { it.startsWith(FEATURE_PREFIX) }
        .toSet()

}