package com.rabbit.hit.ui.main.gamescreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rabbit.hit.R
import com.rabbit.hit.audio.rememberAudioController
import com.rabbit.hit.ui.main.component.SecondaryIconButton
import com.rabbit.hit.ui.main.gamescreen.overlay.GameSettingsOverlay
import com.rabbit.hit.ui.main.gamescreen.overlay.IntroOverlay
import com.rabbit.hit.ui.main.gamescreen.overlay.WinOverlay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GameScreen(
    skin: com.rabbit.hit.data.progress.RabbitSkin,
    onExitToMenu: (GameResult) -> Unit,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val audio = rememberAudioController()

    LaunchedEffect(skin) {
        viewModel.setSkin(skin)
        viewModel.showIntroOnEnter()
    }

    LaunchedEffect(state.running, state.isPaused, state.isGameOver) {
        while (state.running && !state.isPaused && !state.isGameOver) {
            delay(16)
            viewModel.tick()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                GameEvent.CoinCollected -> audio.playCoinPickup()
                GameEvent.GameOver -> audio.playGameLose()
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
        if (!state.isPaused && !state.isGameOver) viewModel.pause()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(state.running, state.isPaused, state.isGameOver) {
                detectTapGestures(onTap = { viewModel.throwCarrot() })
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_game),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize()) {
            GameHud(
                score = state.score,
                coins = state.coins,
                multiplier = state.multiplier,
                onPause = viewModel::pause
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                RotatingBasket(
                    angle = state.basketAngle,
                    carrots = state.carrots,
                )
                ThrowingCarrot(
                    triggerKey = state.throwId,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 86.dp)
                )
                Image(
                    painter = painterResource(id = skin.gameRes),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                        .size(200.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        if (state.showIntro) {
            IntroOverlay(onStart = viewModel::startRun)
        }

        if (state.isPaused && !state.isGameOver) {
            GameSettingsOverlay(
                onResume = viewModel::resume,
                onRetry = viewModel::retry,
                onHome = { onExitToMenu(viewModel.currentResult()) }
            )
        }

        if (state.isGameOver) {
            WinOverlay(
                result = viewModel.currentResult(),
                isWin = false,
                onRetry = viewModel::retry,
                onHome = {
                    val result = viewModel.currentResult()
                    viewModel.consumeResult()
                    onExitToMenu(result)
                }
            )
        }
    }
}

@Composable
private fun GameHud(score: Int, coins: Int, multiplier: Int, onPause: () -> Unit) {
    val shape = RoundedCornerShape(24.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFFFB95F), Color(0xFFFFD196))
                )
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Image(painter = painterResource(id = R.drawable.coin_placeholder), contentDescription = null, modifier = Modifier.size(26.dp))
            Text(text = "$coins", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "$score", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            Text(text = "x$multiplier", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        SecondaryIconButton(onClick = onPause, modifier = Modifier.size(48.dp)) {
            Icon(imageVector = Icons.Default.Pause, contentDescription = null, tint = Color.White, modifier = Modifier.fillMaxSize(0.7f))
        }
    }
}

@Composable
private fun RotatingBasket(angle: Float, carrots: List<GameViewModel.CarrotPin>) {
    val basketSize = 260.dp
    val carrotSize = 46.dp
    val density = LocalDensity.current
    val radiusPx = with(density) { (basketSize / 2).toPx() }

    Box(contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.central_ellipse),
            contentDescription = null,
            modifier = Modifier
                .size(basketSize)
                .rotate(angle)
        )
        carrots.forEach { pin ->
            val totalAngle = pin.angle + angle
            val radians = Math.toRadians(totalAngle.toDouble())
            val offsetX = (cos(radians) * radiusPx).toFloat()
            val offsetY = (sin(radians) * radiusPx).toFloat()

            Image(
                painter = painterResource(id = R.drawable.carrot),
                contentDescription = null,
                modifier = Modifier
                    .size(carrotSize)
                    .graphicsLayer {
                        translationX = offsetX
                        translationY = offsetY
                        rotationZ = totalAngle + 90f
                    },
            )
        }
    }
}

@Composable
private fun ThrowingCarrot(triggerKey: Int, modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val travelDistancePx = remember { Animatable(0f) }
    val scale = remember { Animatable(0f) }
    val spin = remember { Animatable(0f) }

    LaunchedEffect(triggerKey) {
        if (triggerKey == 0) return@LaunchedEffect
        val travel = with(density) { 260.dp.toPx() }
        coroutineScope {
            launch { scale.snapTo(0.2f) }
            launch { travelDistancePx.snapTo(0f) }
            launch { spin.snapTo(0f) }
        }
        coroutineScope {
            launch { scale.animateTo(1f, tween(durationMillis = 140, easing = LinearOutSlowInEasing)) }
            launch {
                travelDistancePx.animateTo(
                    targetValue = -travel,
                    animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing)
                )
            }
            launch { spin.animateTo(targetValue = -360f, animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing)) }
        }
        scale.animateTo(0f, tween(durationMillis = 140, easing = LinearOutSlowInEasing))
    }

    if (triggerKey == 0) return

    Image(
        painter = painterResource(id = R.drawable.carrot),
        contentDescription = null,
        modifier = modifier
            .size(54.dp)
            .graphicsLayer {
                translationY = travelDistancePx.value
                rotationZ = spin.value
                val clampedScale = scale.value.coerceIn(0f, 1.2f)
                scaleX = clampedScale
                scaleY = clampedScale
            },
        contentScale = ContentScale.Fit
    )
}
