package n7.mev.ui.heroes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import n7.mev.R
import n7.mev.ui.common.ProgressTrack
import n7.mev.ui.common.RoundButtonStyle
import n7.mev.ui.common.RoundIconButton
import n7.mev.ui.common.rememberPortrait
import n7.mev.ui.heroes.vo.HeroUI
import n7.mev.ui.theme.MevColors
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ModulesSheet(
    modules: List<HeroUI>,
    download: HeroesViewModel.Download?,
    onInstall: (hero: HeroUI) -> Unit,
    onCancelDownload: () -> Unit,
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
                Text(stringResource(R.string.available_modules).uppercase(), style = MaterialTheme.typography.titleLarge)
                Text(
                    text = stringResource(R.string.available_modules_summary),
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
        LazyColumn(
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(modules, key = { it.moduleName }) { hero ->
                if (download?.moduleName == hero.moduleName) {
                    DownloadingModuleRow(hero, download, onCancelDownload)
                } else {
                    ModuleRow(hero, onInstall = { onInstall(hero) })
                }
            }
        }
    }
}

@Composable
private fun ModuleRow(hero: HeroUI, onInstall: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HeroAvatar(hero)
        Spacer(Modifier.width(14.dp))
        Text(hero.name.uppercase(), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        OutlinedButton(
            onClick = onInstall,
            modifier = Modifier.height(40.dp),
            border = BorderStroke(1.dp, MevColors.Accent),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            Icon(
                painterResource(R.drawable.ic_download),
                contentDescription = null,
                tint = MevColors.AccentText,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.install).uppercase(),
                style = MaterialTheme.typography.titleSmall,
                color = MevColors.AccentText,
            )
        }
    }
}

@Composable
private fun DownloadingModuleRow(hero: HeroUI, download: HeroesViewModel.Download, onCancel: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HeroAvatar(hero)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(hero.name.uppercase(), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Text(
                    text = "${(download.progress * 100).roundToInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MevColors.AccentText,
                )
            }
            ProgressTrack(progress = { download.progress })
        }
        Spacer(Modifier.width(14.dp))
        RoundIconButton(
            icon = R.drawable.ic_close,
            contentDescription = stringResource(R.string.cancel_download),
            onClick = onCancel,
            style = RoundButtonStyle.Outlined,
            iconSize = 16.dp,
        )
    }
}

@Composable
private fun HeroAvatar(hero: HeroUI) {
    val modifier = Modifier
        .size(56.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.outline)
    val portrait = rememberPortrait(hero.icon)
    if (portrait != null) {
        Image(
            bitmap = portrait,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = modifier,
        )
    } else {
        Spacer(modifier)
    }
}
