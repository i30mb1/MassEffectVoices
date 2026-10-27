package n7.mev.ui.support

import android.app.Activity
import android.app.Application
import androidx.compose.runtime.Immutable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import n7.mev.appGraph
import n7.mev.data.billing.BillingRepository

class SupportViewModel(
    application: Application
) : AndroidViewModel(application) {

    @Immutable
    data class UiState(
        val products: BillingRepository.Products = BillingRepository.Products.Loading,
        val isSupporter: Boolean = false,
        val lastEvent: BillingRepository.Event? = null,
    )

    private val billingRepository = application.appGraph.billingRepository
    private val lastEvent = MutableStateFlow<BillingRepository.Event?>(null)

    val state: StateFlow<UiState> = combine(
        billingRepository.products,
        billingRepository.isSupporter,
        lastEvent,
        ::UiState,
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState(isSupporter = billingRepository.isSupporter.value))

    init {
        billingRepository.events
            .onEach { lastEvent.value = it }
            .launchIn(viewModelScope)
    }

    fun onSheetOpened() {
        lastEvent.value = null
        billingRepository.refreshProducts()
    }

    fun buy(activity: Activity, offer: BillingRepository.TierOffer) {
        lastEvent.value = null
        billingRepository.launchPurchase(activity, offer)
    }

}
