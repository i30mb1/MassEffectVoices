package n7.mev.data.billing

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.core.content.edit
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * "Support the developer" one-time products. They are consumable so the same tier can be bought again;
 * the only thing a purchase unlocks is the local supporter flag (no game content is sold).
 */
class BillingRepository(
    application: Application,
    private val scope: CoroutineScope,
) {

    enum class Tier(val productId: String) {
        Coffee("support_coffee"),
        Pizza("support_pizza"),
        Normandy("support_normandy"),
    }

    data class TierOffer(val tier: Tier, val price: String, internal val details: ProductDetails)

    sealed interface Products {
        data object Loading : Products
        data object Unavailable : Products
        data class Ready(val offers: List<TierOffer>) : Products
    }

    sealed interface Event {
        data object Thanks : Event
        data object Pending : Event
        data object Failed : Event
    }

    private val prefs = application.getSharedPreferences("billing", Context.MODE_PRIVATE)

    private val _products = MutableStateFlow<Products>(Products.Loading)
    val products: StateFlow<Products> = _products.asStateFlow()

    // consumed purchases disappear from Play, so the flag lives locally
    private val _isSupporter = MutableStateFlow(prefs.getBoolean(KEY_SUPPORTER, false))
    val isSupporter: StateFlow<Boolean> = _isSupporter.asStateFlow()

    private val _events = Channel<Event>(Channel.BUFFERED)
    val events: Flow<Event> = _events.receiveAsFlow()

    private val client = BillingClient.newBuilder(application)
        .setListener { result, purchases -> onPurchasesUpdated(result, purchases) }
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .enableAutoServiceReconnection()
        .build()

    init {
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingResponseCode.OK) {
                    scope.launch { restorePurchases() }
                } else {
                    _products.value = Products.Unavailable
                }
            }

            // enableAutoServiceReconnection() reconnects on the next call by itself
            override fun onBillingServiceDisconnected() = Unit
        })
    }

    /** Loads fresh product details: cached ProductDetails can make launchBillingFlow() fail. */
    fun refreshProducts() {
        scope.launch {
            if (_products.value !is Products.Ready) _products.value = Products.Loading
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    Tier.entries.map { tier ->
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(tier.productId)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    }
                )
                .build()
            val result = client.queryProductDetails(params)
            val details = result.productDetailsList.orEmpty().associateBy { it.productId }
            val offers = Tier.entries.mapNotNull { tier ->
                val product = details[tier.productId] ?: return@mapNotNull null
                val offer = product.oneTimePurchaseOfferDetailsList?.firstOrNull() ?: return@mapNotNull null
                TierOffer(tier, offer.formattedPrice, product)
            }
            _products.value = if (offers.isEmpty()) Products.Unavailable else Products.Ready(offers)
        }
    }

    fun launchPurchase(activity: Activity, offer: TierOffer) {
        val offerToken = offer.details.oneTimePurchaseOfferDetailsList?.firstOrNull()?.offerToken ?: return
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(offer.details)
                        .setOfferToken(offerToken)
                        .build()
                )
            )
            .build()
        val result = client.launchBillingFlow(activity, params)
        if (result.responseCode != BillingResponseCode.OK) _events.trySend(Event.Failed)
    }

    private fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        when (result.responseCode) {
            BillingResponseCode.OK -> purchases.orEmpty().forEach { scope.launch { handle(it, notify = true) } }
            // since PBL 8.3 leaving for an external payment app without returning is USER_CANCELED as well
            BillingResponseCode.USER_CANCELED -> Unit
            else -> _events.trySend(Event.Failed)
        }
    }

    /** Finishes purchases that were paid but not consumed: app killed mid-flow or a pending payment that completed. */
    private suspend fun restorePurchases() {
        val params = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build()
        val result = client.queryPurchasesAsync(params)
        if (result.billingResult.responseCode == BillingResponseCode.OK) {
            result.purchasesList.forEach { handle(it, notify = false) }
        }
    }

    private suspend fun handle(purchase: Purchase, notify: Boolean) {
        when (purchase.purchaseState) {
            Purchase.PurchaseState.PURCHASED -> {
                val params = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
                val consumed = client.consumePurchase(params).billingResult.responseCode == BillingResponseCode.OK
                // a purchase that is neither consumed nor acknowledged is refunded after 3 days
                if (!consumed) {
                    if (notify) _events.send(Event.Failed)
                    return
                }
                prefs.edit { putBoolean(KEY_SUPPORTER, true) }
                _isSupporter.value = true
                if (notify) _events.send(Event.Thanks)
            }

            Purchase.PurchaseState.PENDING -> if (notify) _events.send(Event.Pending)
            else -> Unit
        }
    }

    private companion object {
        const val KEY_SUPPORTER = "supporter"
    }

}
