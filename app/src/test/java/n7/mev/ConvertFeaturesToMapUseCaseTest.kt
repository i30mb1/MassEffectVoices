package n7.mev

import com.google.common.truth.Truth.assertThat
import n7.mev.ui.heroes.vo.HeroVO
import org.junit.Test

class ConvertFeaturesToMapUseCaseTest {

    val convertFeatureModuleToMap = ConvertFeaturesToMapUseCase()

    @Test
    fun `fields in map equal to object`() {
        val featureModule = HeroVO("Avina", "feature_avina")

        val map: Map<String,String> = convertFeatureModuleToMap(listOf(featureModule))

        assertThat(featureModule.featureName).isEqualTo(map[featureModule.name])
    }

}