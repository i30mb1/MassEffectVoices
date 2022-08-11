@file:Suppress("unused")

import kotlin.reflect.full.memberProperties

const val FEATURE_PREFIX = ":feature"

object ModuleDependency {
    const val APP = ":app"
    const val AVINA = "${FEATURE_PREFIX}:avina"
    const val EDI = "${FEATURE_PREFIX}:edi"
    const val GARRUS = "${FEATURE_PREFIX}:garrus"
    const val GRUNT = "${FEATURE_PREFIX}:grunt"
    const val WREX = "${FEATURE_PREFIX}:wrex"
    const val ILLUSIVE_MAN = "${FEATURE_PREFIX}:illusive_man"
    const val JACK = "${FEATURE_PREFIX}:jack"
    const val JACOB = "${FEATURE_PREFIX}:jacob"
    const val JAVIK = "${FEATURE_PREFIX}:javik"
    const val JOKER = "${FEATURE_PREFIX}:joker"
    const val KASUMI = "${FEATURE_PREFIX}:kasumi"
    const val LEGION = "${FEATURE_PREFIX}:legion"
    const val LIARA = "${FEATURE_PREFIX}:liara"
    const val MIRANDA = "${FEATURE_PREFIX}:miranda"
    const val MORDIN = "${FEATURE_PREFIX}:mordin"
    const val REAPER = "${FEATURE_PREFIX}:reaper"
    const val SAMARA = "${FEATURE_PREFIX}:samara"
    const val TALI = "${FEATURE_PREFIX}:tali"
    const val THANE = "${FEATURE_PREFIX}:thane"
    const val VEGA = "${FEATURE_PREFIX}:vega"
    const val ZAEED = "${FEATURE_PREFIX}:zaeed"
    const val ANDERSON = "${FEATURE_PREFIX}:anderson"
    const val KAIDAN = "${FEATURE_PREFIX}:kaidan"
    const val ASHLEY = "${FEATURE_PREFIX}:ashley"
    const val RACHNI_QUEEN = "${FEATURE_PREFIX}:rachni_queen"

    private fun getAllModules() = ModuleDependency::class.memberProperties
        .filter { it.isConst }
        .map { it.getter.call().toString() }
        .toSet()

    fun getDynamicFeatureModules() = getAllModules()
        .filter { it.startsWith(FEATURE_PREFIX) }
        .toSet()

    fun getDynamicFeatureModulesNames() = getDynamicFeatureModules()
        .map { it.drop(FEATURE_PREFIX.length + 1) }
        .toSet()

}