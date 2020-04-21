package n7.mev

import n7.mev.data.source.local.FeatureModule

class ConvertFeaturesToMapUseCase {
    operator fun invoke(list: List<FeatureModule>): Map<String, String> {
        val map = mutableMapOf<String,String>()
        list.forEach {
            map[it.name] = it.featureName
        }
        return map
    }
}