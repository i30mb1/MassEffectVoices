package n7.mev.ui.support

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import n7.mev.R
import n7.mev.data.billing.BillingRepository
import n7.mev.data.billing.BillingRepository.Tier
import n7.mev.ui.common.RoundIconButton
import n7.mev.ui.theme.MevColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SupportSheet(
    state: SupportViewModel.UiState,
    onBuy: (offer: BillingRepository.TierOffer) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = MevColors.Scrim.copy(alpha = 0.75f),
    ) {
        Row(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringResource(R.string.support_title).uppercase(), style = MaterialTheme.typography.titleLarge)
                Text(
                    text = stringResource(R.string.support_summary),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            RoundIconButton(
                icon = R.drawable.ic_close,
                contentDescription = stringResource(R.string.close),
                onClick = onDismiss,
            )
        }
        Column(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            state.lastEvent?.let { EventBanner(it) }
            when (val products = state.products) {
                BillingRepository.Products.Loading -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MevColors.Accent, modifier = Modifier.size(28.dp))
                }

                BillingRepository.Products.Unavailable -> Text(
                    text = stringResource(R.string.support_unavailable),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                )

                is BillingRepository.Products.Ready -> products.offers.forEach { offer ->
                    TierRow(offer, onBuy = { onBuy(offer) })
                }
            }
            PrivacyPolicyLink()
        }
    }
}

@Composable
private fun PrivacyPolicyLink() {
    val uriHandler = LocalUriHandler.current
    TextButton(
        onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) },
        modifier = Modifier.padding(top = 8.dp),
    ) {
        Text(
            text = stringResource(R.string.privacy_policy),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EventBanner(event: BillingRepository.Event) {
    val text = when (event) {
        BillingRepository.Event.Thanks -> R.string.support_thanks
        BillingRepository.Event.Pending -> R.string.support_pending
        BillingRepository.Event.Failed -> R.string.action_failed
    }
    Text(
        text = stringResource(text),
        style = MaterialTheme.typography.bodyLarge,
        color = if (event == BillingRepository.Event.Failed) MaterialTheme.colorScheme.onSurfaceVariant else MevColors.AccentText,
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    )
}

@Composable
private fun TierRow(offer: BillingRepository.TierOffer, onBuy: () -> Unit) {
    val (title, summary) = when (offer.tier) {
        Tier.Coffee -> R.string.support_coffee to R.string.support_coffee_summary
        Tier.Pizza -> R.string.support_pizza to R.string.support_pizza_summary
        Tier.Normandy -> R.string.support_normandy to R.string.support_normandy_summary
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(stringResource(title).uppercase(), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(summary),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.width(14.dp))
        OutlinedButton(
            onClick = onBuy,
            modifier = Modifier.height(40.dp),
            border = BorderStroke(1.dp, MevColors.Accent),
        ) {
            Text(text = offer.price, style = MaterialTheme.typography.titleSmall, color = MevColors.AccentText)
        }
    }
}

private const val PRIVACY_POLICY_URL = "https://i30mb1.github.io/MassEffectVoices/privacy-policy.html"
