package n7.mev.ui.heroes

import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import n7.mev.R
import n7.mev.data.source.local.MePart
import n7.mev.ui.common.ProgressTrack
import n7.mev.ui.common.RoundIconButton
import n7.mev.ui.common.n7Stripes
import n7.mev.ui.common.rememberPortrait
import n7.mev.ui.heroes.vo.HeroUI
import n7.mev.ui.support.SupportSheet
import n7.mev.ui.support.SupportViewModel
import n7.mev.ui.theme.MevColors
import n7.mev.ui.theme.MevTheme
import kotlin.math.roundToInt

@Composable
fun HeroesRoute(
    viewModel: HeroesViewModel,
    onHeroClicked: (hero: HeroUI) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val supportViewModel: SupportViewModel = viewModel()
    val supportState by supportViewModel.state.collectAsStateWithLifecycle()
    val activity = LocalActivity.current
    var isModulesSheetOpen by rememberSaveable { mutableStateOf(false) }
    var isSupportSheetOpen by rememberSaveable { mutableStateOf(false) }
    val confirmationLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {}

    LaunchedEffect(viewModel) {
        viewModel.confirmations.collect { sessionState ->
            viewModel.startConfirmationDialog(sessionState, confirmationLauncher)
        }
    }

    HeroesScreen(
        state = state,
        onHeroClicked = onHeroClicked,
        isSupporter = supportState.isSupporter,
        onAddModuleClicked = { isModulesSheetOpen = true },
        onSupportClicked = {
            supportViewModel.onSheetOpened()
            isSupportSheetOpen = true
        },
    )

    if (isModulesSheetOpen) {
        ModulesSheet(
            modules = state.available,
            download = state.download,
            onInstall = { hero -> viewModel.installModule(hero.moduleName) },
            onCancelDownload = viewModel::cancelDownload,
            onDismiss = { isModulesSheetOpen = false },
        )
    }

    if (isSupportSheetOpen) {
        SupportSheet(
            state = supportState,
            onBuy = { offer -> activity?.let { supportViewModel.buy(it, offer) } },
            onDismiss = { isSupportSheetOpen = false },
        )
    }
}

@Composable
internal fun HeroesScreen(
    state: HeroesViewModel.UiState,
    isSupporter: Boolean,
    onHeroClicked: (hero: HeroUI) -> Unit,
    onAddModuleClicked: () -> Unit,
    onSupportClicked: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (state.available.isNotEmpty()) AddModuleBar(onAddModuleClicked)
        },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 16.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                HeroesHeader(
                    installedCount = state.installed.size,
                    isSupporter = isSupporter,
                    onSupportClicked = onSupportClicked,
                )
            }
            state.download?.let { download ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    DownloadBanner(download)
                }
            }
            items(state.installed, key = { it.moduleName }) { hero ->
                HeroCard(hero = hero, onClick = { onHeroClicked(hero) })
            }
        }
    }
}

@Composable
private fun HeroesHeader(installedCount: Int, isSupporter: Boolean, onSupportClicked: () -> Unit) {
    Row(
        modifier = Modifier.padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_n7_logo),
            contentDescription = null,
            modifier = Modifier.size(width = 40.dp, height = 35.dp),
        )
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.squad).uppercase(),
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = pluralStringResource(R.plurals.modules_installed, installedCount, installedCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (isSupporter) {
            Text(
                text = stringResource(R.string.supporter_badge).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MevColors.AccentText,
                modifier = Modifier
                    .border(1.dp, MevColors.Accent, MaterialTheme.shapes.small)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
        RoundIconButton(
            icon = R.drawable.ic_support,
            contentDescription = stringResource(R.string.support_title),
            onClick = onSupportClicked,
        )
    }
}

@Composable
private fun DownloadBanner(download: HeroesViewModel.Download) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.ic_download),
                contentDescription = null,
                tint = MevColors.AccentText,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.downloading_module, download.heroName),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${(download.progress * 100).roundToInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        ProgressTrack(progress = { download.progress })
    }
}

@Composable
private fun HeroCard(hero: HeroUI, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MevColors.CardBorder, MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(148.dp)
                .background(MevColors.SurfaceDim)
                .n7Stripes(offset = 92.dp, width = 26.dp, accentWidth = 8.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            rememberPortrait(hero.icon)?.let { portrait ->
                Image(
                    bitmap = portrait,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(150.dp),
                )
            }
        }
        Column(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = hero.name.uppercase(), style = MaterialTheme.typography.titleMedium, maxLines = 1)
            Text(
                text = hero.parts.joinToString(" · ").ifEmpty { " " },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AddModuleBar(onClick: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column {
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainer)
            Button(
                onClick = onClick,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            ) {
                Icon(painterResource(R.drawable.ic_add), contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.add_module).uppercase(), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Preview
@Composable
private fun HeroesScreenPreview() {
    MevTheme {
        HeroesScreen(
            state = HeroesViewModel.UiState(
                installed = listOf(
                    HeroUI("Garrus", R.drawable.garrus, "garrus", MePart.entries),
                    HeroUI("Tali", R.drawable.tali, "tali", MePart.entries),
                    HeroUI("Legion", R.drawable.legion, "legion", listOf(MePart.ME2, MePart.ME3)),
                ),
                available = listOf(HeroUI("Mordin", R.drawable.mordin, "mordin")),
                download = HeroesViewModel.Download(0, "mordin", "Mordin", 0.42f),
            ),
            isSupporter = true,
            onHeroClicked = {},
            onAddModuleClicked = {},
            onSupportClicked = {},
        )
    }
}
