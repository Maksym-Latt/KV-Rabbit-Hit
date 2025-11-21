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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.geometry.Offset
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GameScreen(
    skin: com.rabbit.hit.data.progress.RabbitSkin,
    onExitToMenu: (GameResult) -> Unit,
    viewModel: GameViewModel = hiltViewModel(),
    debugHitboxes: Boolean = DEBUG_DRAW_HITBOXES,
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

    val launchOffset = remember { mutableStateOf<Offset?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(state.running, state.isPaused, state.isGameOver) {
                detectTapGestures(onTap = { offset ->
                    val center = Offset(size.width / 2f, size.height / 2f)
                    launchOffset.value = Offset(offset.x - center.x, offset.y - center.y)
                    viewModel.throwCarrot()
                })
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
                    debugHitboxes = debugHitboxes,
                )
                ThrowingCarrot(
                    triggerKey = state.flight?.id ?: 0,
                    isActive = state.flight != null,
                    startOffset = launchOffset.value,
                    onFlightFinished = viewModel::resolveFlightIfReady,
                    modifier = Modifier.align(Alignment.Center)
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

private const val DEBUG_DRAW_HITBOXES = false
private val BasketSize = 260.dp
private val PinnedCarrotSize = 46.dp
private val ThrowingCarrotSize = 54.dp

@Composable
private fun RotatingBasket(angle: Float, carrots: List<GameViewModel.CarrotPin>, debugHitboxes: Boolean) {
    val density = LocalDensity.current
    val radiusPx = with(density) { (BasketSize / 2).toPx() }

    Box(contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.central_ellipse),
            contentDescription = null,
            modifier = Modifier
                .size(BasketSize)
                .rotate(angle)
        )
        if (debugHitboxes) {
            androidx.compose.foundation.Canvas(modifier = Modifier.size(BasketSize)) {
                val strokeWidth = 3.dp.toPx()
                val targetStart = TARGET_ANGLE - COLLISION_THRESHOLD
                val sweep = COLLISION_THRESHOLD * 2
                drawCircle(
                    color = Color(0x8032CD32),
                    radius = radiusPx,
                    style = Stroke(width = strokeWidth)
                )
                drawArc(
                    color = Color(0x8032CD32),
                    startAngle = targetStart,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = strokeWidth * 1.4f)
                )
                carrots.forEach { pin ->
                    val carrotWorldAngle = pin.angle + angle
                    drawArc(
                        color = Color(0x80FF4500),
                        startAngle = carrotWorldAngle - COLLISION_THRESHOLD,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(width = strokeWidth)
                    )
                }
            }
        }
        carrots.forEach { pin ->
            val totalAngle = pin.angle + angle
            val radians = Math.toRadians(totalAngle.toDouble())
            val offsetX = (cos(radians) * radiusPx).toFloat()
            val offsetY = (sin(radians) * radiusPx).toFloat()

            Image(
                painter = painterResource(id = R.drawable.carrot),
                contentDescription = null,
                modifier = Modifier
                    .size(PinnedCarrotSize)
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
private fun ThrowingCarrot(
    triggerKey: Int,
    isActive: Boolean,
    startOffset: Offset?,
    onFlightFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val progress = remember(triggerKey) { Animatable(0f) }
    val defaultStartOffset = remember { Offset(0f, with(density) { 220.dp.toPx() }) }
    val animatedStart = remember { mutableStateOf(defaultStartOffset) }

    val radiusPx = remember { with(density) { (BasketSize / 2).toPx() } }
    val targetOffset = remember {
        val radians = Math.toRadians(TARGET_ANGLE.toDouble())
        Offset((cos(radians) * radiusPx).toFloat(), (sin(radians) * radiusPx).toFloat())
    }
    val carrotRotation = TARGET_ANGLE + 90f

    LaunchedEffect(triggerKey) {
        if (triggerKey == 0 || !isActive) return@LaunchedEffect
        animatedStart.value = startOffset ?: defaultStartOffset

        coroutineScope {
            launch { progress.snapTo(0f) }
            launch {
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = CARROT_FLIGHT_DURATION_MS.toInt(),
                        easing = LinearEasing
                    )
                )
                onFlightFinished()
            }
        }
    }

    if (triggerKey == 0 || !isActive) return

    val animatedOffset = Offset(
        x = androidx.compose.ui.util.lerp(animatedStart.value.x, targetOffset.x, progress.value),
        y = androidx.compose.ui.util.lerp(animatedStart.value.y, targetOffset.y, progress.value),
    )

    Image(
        painter = painterResource(id = R.drawable.carrot),
        contentDescription = null,
        modifier = modifier
            .size(ThrowingCarrotSize)
            .graphicsLayer {
                translationX = animatedOffset.x
                translationY = animatedOffset.y
                rotationZ = carrotRotation
            },
        contentScale = ContentScale.Fit
    )
}
