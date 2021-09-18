package n7.mev

import n7.mev.ui.heroes.vo.HeroVO

class ConvertFeaturesToMapUseCase {
    operator fun invoke(list: List<HeroVO>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        list.forEach {
            map[it.name] = it.featureName
        }
        return map
    }
}