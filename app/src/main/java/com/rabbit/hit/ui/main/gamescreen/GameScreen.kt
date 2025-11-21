package com.rabbit.hit.ui.main.gamescreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rabbit.hit.R
import com.rabbit.hit.audio.rememberAudioController
import com.rabbit.hit.ui.main.component.SecondaryIconButton
import com.rabbit.hit.ui.main.gamescreen.overlay.GameOverOverlay
import com.rabbit.hit.ui.main.gamescreen.overlay.GameSettingsOverlay
import com.rabbit.hit.ui.main.gamescreen.overlay.IntroOverlay
import com.rabbit.hit.ui.main.gamescreen.overlay.WinOverlay
import kotlinx.coroutines.delay

// ======================= 🐣 ЭКРАН ИГРЫ =======================
@Composable
fun GameScreen(
    skin: EggSkin,
    onExitToMenu: (GameResult) -> Unit,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val audio = rememberAudioController()

    LaunchedEffect(skin) {
        viewModel.setSkin(skin)
        viewModel.showIntroOnEnter()
    }

    LaunchedEffect(state.running, state.isPaused, state.isGameOver, state.hasWon) {
        while (state.running && !state.isPaused && !state.isGameOver && !state.hasWon) {
            delay(16)
            viewModel.tick()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                GameEvent.Jumped -> audio.playJump()
                GameEvent.CoinCollected -> audio.playCoinPickup()
                GameEvent.GameOver -> audio.playGameLose()
                GameEvent.Win -> audio.playGameWin()
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                val current = viewModel.state.value
                if (current.running && !current.isPaused && !current.isGameOver) {
                    viewModel.pause()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler(enabled = state.running && !state.showIntro) {
        if (!state.isPaused && !state.isGameOver) {
            viewModel.pause()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        GameField(
            state = state,
            onDrag = viewModel::movePlayer
        )

        GameHud(
            coins = state.coins,
            height = state.height,
            onPause = viewModel::pause
        )

        if (state.showIntro) {
            IntroOverlay(
                level = state.level,
                targetCoins = state.targetCoins,
                onStart = viewModel::startRun
            )
        }

        if (state.isPaused && !state.isGameOver) {
            GameSettingsOverlay(
                onResume = viewModel::resume,
                onRetry = { viewModel.retry() },
                onHome = { onExitToMenu(viewModel.currentResult()) }
            )
        }

        if (state.hasWon) {
            WinOverlay(
                result = viewModel.currentResult(),
                onNextLevel = viewModel::advanceToNextLevel,
                onHome = {
                    val result = viewModel.currentResult()
                    viewModel.advanceToNextLevel()
                    onExitToMenu(result)
                }
            )
        } else if (state.isGameOver) {
            GameOverOverlay(
                result = viewModel.currentResult(),
                targetCoins = state.targetCoins,
                onRetry = { viewModel.retry() },
                onHome = { onExitToMenu(viewModel.currentResult()) }
            )
        }
    }
}

@Composable
private fun GameHud(
    coins: Int,
    height: Int,
    onPause: () -> Unit,
) {
    val cardShape = RoundedCornerShape(24.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp).windowInsetsPadding(WindowInsets.displayCutout)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = cardShape,
                    clip = false,
                    ambientColor = Color(0x33000000),
                    spotColor = Color(0x33000000)
                )
                .clip(cardShape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFE9D7FF),
                            Color(0xFFFFE6CF)
                        )
                    )
                )
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- монеты ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.coin),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "x$coins",
                    color = Color(0xFF7B4A2D),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }

            // --- высота + стрелка вверх ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${height}m",
                    color = Color(0xFF7B4A2D),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = Color(0xFF7B4A2D),
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .size(18.dp)
                )
            }

            // --- кнопка паузы в круге ---
            SecondaryIconButton(
                onClick = onPause,
                modifier = Modifier.size(46.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = Color(0xFFFF9154),
                    modifier = Modifier.fillMaxSize(0.7f)
                )
            }
        }
    }
}
