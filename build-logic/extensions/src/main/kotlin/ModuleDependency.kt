@file:Suppress("unused")

import kotlin.reflect.full.memberProperties

object ModuleDependency {
    const val APP = ":app"
    const val AVINA = ":feature:avina"
    const val EDI = ":feature:edi"
    const val GARRUS = ":feature:garrus"
    const val GRUNT = ":feature:grunt"
    const val WREX = ":feature:wrex"
    const val ILLUSIVE_MAN = ":feature:illusive_man"
    const val JACK = ":feature:jack"
    const val JACOB = ":feature:jacob"
    const val JAVIK = ":feature:javik"
    const val JOKER = ":feature:joker"
    const val KASUMI = ":feature:kasumi"
    const val LEGION = ":feature:legion"
    const val LIARA = ":feature:liara"
    const val MIRANDA = ":feature:miranda"
    const val MORDIN = ":feature:mordin"
    const val REAPER = ":feature:reaper"
    const val SAMARA = ":feature:samara"
    const val TALI = ":feature:tali"
    const val THANE = ":feature:thane"
    const val VEGA = ":feature:vega"
    const val ZAEED = ":feature:zaeed"
    const val ANDERSON = ":feature:anderson"
    const val KAIDAN = ":feature:kaidan"
    const val ASHLEY = ":feature:ashley"
    const val RACHNI_QUEEN = ":feature:rachni_queen"

    private fun getAllModules() = ModuleDependency::class.memberProperties
        .filter { it.isConst }
        .map { it.getter.call().toString() }
        .toSet()

    fun getDynamicFeatureModules() = getAllModules()
        .filter { it.startsWith(":feature") }
        .toSet()

    fun getDynamicFeatureModulesNames() = getDynamicFeatureModules()
        .map { it.drop(":feature".length + 1) }
        .toSet()

}
