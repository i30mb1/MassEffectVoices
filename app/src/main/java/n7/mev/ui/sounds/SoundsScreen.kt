package n7.mev.ui.sounds

import android.content.Intent
import android.provider.Settings
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import n7.mev.R
import n7.mev.data.source.local.MePart
import n7.mev.ui.common.ProgressTrack
import n7.mev.ui.common.RoundButtonStyle
import n7.mev.ui.common.RoundIconButton
import n7.mev.ui.common.n7Stripes
import n7.mev.ui.common.rememberPortrait
import n7.mev.ui.heroes.vo.HeroUI
import n7.mev.ui.sounds.vo.SoundSectionVO
import n7.mev.ui.sounds.vo.SoundVO
import n7.mev.ui.theme.MevColors
import n7.mev.ui.theme.MevTheme

@Composable
fun SoundsRoute(
    moduleName: String,
    onBack: () -> Unit,
    viewModel: SoundViewModel = viewModel(),
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val progress = viewModel.progress.collectAsStateWithLifecycle()
    var actionsFor by remember { mutableStateOf<SoundVO?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(moduleName) { viewModel.load(moduleName) }
    // the player outlives the screen: do not let a line keep playing on the squad screen
    DisposableEffect(viewModel) { onDispose { viewModel.stop() } }
    LaunchedEffect(viewModel) {
        viewModel.messages.collect { message -> snackbarHostState.showSnackbar(resources.getString(message)) }
    }

    Box(Modifier.fillMaxSize()) {
        SoundsScreen(
            state = state,
            progress = { progress.value },
            onBack = onBack,
            onSelectPart = viewModel::selectPart,
            onToggle = viewModel::toggle,
            onMore = { sound -> actionsFor = sound },
            onStop = viewModel::stop,
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 88.dp),
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
            )
        }
    }

    actionsFor?.let { sound ->
        SoundActionsSheet(
            heroName = state.hero?.name.orEmpty(),
            sound = sound,
            onPlay = { viewModel.play(sound) },
            onDownload = {
                viewModel.download(sound)
                actionsFor = null
            },
            onSetRingtone = {
                if (Settings.System.canWrite(context)) {
                    viewModel.setAsRingtone(sound)
                } else {
                    context.startActivity(
                        Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, "package:${context.packageName}".toUri())
                    )
                }
                actionsFor = null
            },
            onDismiss = { actionsFor = null },
        )
    }
}

@Composable
internal fun SoundsScreen(
    state: SoundViewModel.UiState,
    progress: () -> Float,
    onBack: () -> Unit,
    onSelectPart: (MePart) -> Unit,
    onToggle: (SoundVO) -> Unit,
    onMore: (SoundVO) -> Unit,
    onStop: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(contentPadding = PaddingValues(bottom = 140.dp)) {
            item { HeroHeader(state.hero, state.totalLines, state.parts.size, onBack) }
            if (state.parts.size > 1) item {
                PartTabs(state.lineCounts, state.selectedPart, onSelectPart)
            }
            if (state.isLoading) item {
                Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MevColors.Accent)
                }
            }
            for (section in state.sections) {
                item(key = "header:${section.folder}", contentType = "header") { SectionHeader(section) }
                items(section.sounds, key = { it.assetPath }, contentType = { "sound" }) { sound ->
                    SoundRow(
                        sound = sound,
                        isPlaying = sound.assetPath == state.playing?.assetPath,
                        progress = progress,
                        onToggle = { onToggle(sound) },
                        onMore = { onMore(sound) },
                    )
                }
            }
        }
        state.playing?.let { sound ->
            NowPlayingBar(
                sound = sound,
                onStop = onStop,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun HeroHeader(hero: HeroUI?, totalLines: Int, games: Int, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(252.dp)
            .background(MevColors.SurfaceDim)
            .n7Stripes(offset = 236.dp, width = 44.dp, accentWidth = 12.dp),
    ) {
        rememberPortrait(hero?.icon ?: 0)?.let { portrait ->
            Image(
                bitmap = portrait,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 12.dp)
                    .size(230.dp),
            )
        }
        RoundIconButton(
            icon = R.drawable.ic_back,
            contentDescription = stringResource(R.string.back),
            onClick = onBack,
            iconSize = 20.dp,
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 12.dp, top = 8.dp),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 22.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(hero?.name.orEmpty().uppercase(), style = MaterialTheme.typography.displaySmall)
            if (totalLines > 0) Text(
                text = pluralStringResource(R.plurals.lines_count, totalLines, totalLines) +
                    " · " + pluralStringResource(R.plurals.games_count, games, games),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PartTabs(
    lineCounts: Map<MePart, Int>,
    selected: MePart?,
    onSelect: (MePart) -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 6.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MevColors.CardBorder, shape)
            .padding(4.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        lineCounts.forEach { (part, count) ->
            val isSelected = part == selected
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .selectable(selected = isSelected, role = Role.Tab, onClick = { onSelect(part) }),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                Text(part.name, style = MaterialTheme.typography.titleSmall, color = color)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = color.copy(alpha = 0.8f),
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(section: SoundSectionVO) {
    Row(Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 6.dp)) {
        Text(
            text = section.folder,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = section.sounds.size.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MevColors.OnSurfaceVariant.copy(alpha = 0.8f),
        )
    }
}

@Composable
private fun SoundRow(
    sound: SoundVO,
    isPlaying: Boolean,
    progress: () -> Float,
    onToggle: () -> Unit,
    onMore: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 1.dp)
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .clip(MaterialTheme.shapes.small)
            .background(if (isPlaying) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent)
            .clickable(onClick = onToggle)
            .padding(start = 8.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoundIconButton(
            icon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
            contentDescription = stringResource(if (isPlaying) R.string.pause_line else R.string.play_line, sound.lineId),
            onClick = onToggle,
            style = if (isPlaying) RoundButtonStyle.Filled else RoundButtonStyle.Tonal,
            iconSize = 16.dp,
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(stringResource(R.string.line_title, sound.lineId), style = MaterialTheme.typography.bodyLarge)
            if (isPlaying) ProgressTrack(progress = progress, height = 3.dp, track = MevColors.OutlineVariant)
        }
        RoundIconButton(
            icon = R.drawable.ic_more,
            contentDescription = stringResource(R.string.more_actions),
            onClick = onMore,
            style = RoundButtonStyle.Plain,
            iconSize = 20.dp,
        )
    }
}

@Composable
private fun NowPlayingBar(sound: SoundVO, onStop: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            .padding(start = 14.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painterResource(R.drawable.ic_wave),
            contentDescription = null,
            tint = MevColors.AccentText,
            modifier = Modifier.size(28.dp),
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = stringResource(R.string.line_title, sound.lineId),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = sound.folder,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
        RoundIconButton(
            icon = R.drawable.ic_stop,
            contentDescription = stringResource(R.string.stop),
            onClick = onStop,
            style = RoundButtonStyle.Filled,
            iconSize = 14.dp,
        )
    }
}

@Preview
@Composable
private fun SoundsScreenPreview() {
    val sounds = listOf("00129783", "00129784", "00129785").map { id ->
        SoundVO(id, "EN_cha00_trig_garrus_${id}_M.ogg", "cha00_trig_garrus", MePart.ME1, "", "asset:///$id".toUri())
    }
    MevTheme {
        SoundsScreen(
            state = SoundViewModel.UiState(
                isLoading = false,
                hero = HeroUI("Garrus", R.drawable.garrus, "garrus", MePart.entries),
                parts = mapOf(MePart.ME1 to listOf(SoundSectionVO("cha00_trig_garrus", sounds))),
                lineCounts = mapOf(MePart.ME1 to 682, MePart.ME2 to 1119, MePart.ME3 to 1548),
                selectedPart = MePart.ME1,
                playing = sounds[1],
            ),
            progress = { 0.38f },
            onBack = {},
            onSelectPart = {},
            onToggle = {},
            onMore = {},
            onStop = {},
        )
    }
}
