package com.rabbit.hit.ui.main.gamescreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
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
import com.rabbit.hit.ui.main.gamescreen.overlay.GameSettingsOverlay
import com.rabbit.hit.ui.main.gamescreen.overlay.IntroOverlay
import com.rabbit.hit.ui.main.gamescreen.overlay.WinOverlay
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay

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
                GameEvent.GameWin -> audio.playCoinPickup()
                GameEvent.BoostCollected -> audio.playCoinPickup()
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
        modifier =
            Modifier
                    .fillMaxSize()
                    .pointerInput(
                            state.running,
                            state.isPaused,
                            state.isGameOver
                    ) {
                            detectTapGestures(
                                    onTap = { _ ->
                                            launchOffset.value = null
                                            viewModel.throwCarrot()
                                    }
                            )
                    }
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_game),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        GameHud(
            score = state.score,
            targetScore = state.targetScore,
            coins = state.coins,
            multiplier = state.multiplier,
            onPause = viewModel::pause,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        Playfield(
            state = state,
            startOffset = launchOffset.value,
            onFlightFinished = viewModel::resolveFlightIfReady,
            debugHitboxes = debugHitboxes,
            skin = skin,
            modifier = Modifier.fillMaxSize()
        )

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
                isWin = state.isWin,
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
private fun GameHud(
    score: Int,
    targetScore: Int,
    coins: Int,
    multiplier: Int,
    onPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OrangePill {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.coin_placeholder),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "$coins",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }
        }

        OrangePill(shape = RoundedCornerShape(18.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "$score / $targetScore",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp
                )
                Text(
                    text = "x$multiplier",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        SecondaryIconButton(onClick = onPause, modifier = Modifier.size(52.dp)) {
            Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.fillMaxSize(0.7f)
            )
        }
    }
}

@Composable
private fun OrangePill(
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier =
            Modifier
                    .clip(shape)
                    .background(Color(0xFFFFA53A))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        content = content
    )
}

@Composable
private fun Playfield(
    state: GameViewModel.GameUiState,
    startOffset: Offset?,
    onFlightFinished: () -> Unit,
    debugHitboxes: Boolean,
    skin: com.rabbit.hit.data.progress.RabbitSkin,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(420.dp),
            contentAlignment = Alignment.Center
        ) {
            ThrowingCarrot(
                triggerKey = state.flight?.id ?: 0,
                isActive = state.flight != null,
                isBouncing = state.flight?.bouncing ?: false,
                onFlightFinished = onFlightFinished,
                modifier = Modifier.align(Alignment.TopCenter)
            )
            RotatingBasket(
                angle = state.basketAngle,
                carrots = state.carrots,
                orbitingItems = state.orbitingItems,
                activeBoost = state.activeBoost,
                debugHitboxes = debugHitboxes,
            )
        }

        Row(
            modifier =
                Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = skin.gameRes),
                contentDescription = null,
                modifier = Modifier
                        .padding(bottom = 6.dp)
                        .size(190.dp),
                contentScale = ContentScale.Fit
            )
            CarrotPile()
        }
    }
}

@Composable
private fun CarrotPile() {
    Box(modifier = Modifier
            .padding(start = 12.dp)
            .size(width = 90.dp, height = 80.dp)) {
        Image(
            painter = painterResource(id = R.drawable.carrot),
            contentDescription = null,
            modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(46.dp)
                    .rotate(-8f),
            contentScale = ContentScale.Fit
        )
        Image(
            painter = painterResource(id = R.drawable.carrot),
            contentDescription = null,
            modifier =
                Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .size(50.dp)
                        .rotate(12f),
            contentScale = ContentScale.Fit
        )
        Image(
            painter = painterResource(id = R.drawable.carrot),
            contentDescription = null,
            modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(42.dp)
                    .rotate(-24f),
            contentScale = ContentScale.Fit
        )
    }
}

private const val DEBUG_DRAW_HITBOXES = false
private val BasketSize = 260.dp
private val PinnedCarrotSize = 82.dp
private val ThrowingCarrotSize = 80.dp

@Composable
private fun RotatingBasket(
    angle: Float,
    carrots: List<GameViewModel.CarrotPin>,
    orbitingItems: List<GameViewModel.OrbitingItem>,
    activeBoost: GameViewModel.ActiveBoost?,
    debugHitboxes: Boolean
) {
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

        // Render orbiting items (coins and boosts)
        orbitingItems.forEach { item ->
            val totalAngle = item.angle + angle
            val radians = Math.toRadians(totalAngle.toDouble())
            val offsetX = (cos(radians) * radiusPx).toFloat()
            val offsetY = (sin(radians) * radiusPx).toFloat()

            when (item.type) {
                GameViewModel.ItemType.COIN -> {
                    Image(
                        painter =
                            painterResource(
                                id = R.drawable.coin_placeholder
                            ),
                        contentDescription = null,
                        modifier =
                            Modifier
                                    .size(40.dp)
                                    .graphicsLayer {
                                            translationX = offsetX
                                            translationY = offsetY
                                    }
                    )
                }

                GameViewModel.ItemType.BOOST_X2,
                GameViewModel.ItemType.BOOST_X5 -> {
                    val boostText =
                        if (item.type == GameViewModel.ItemType.BOOST_X2)
                            "x2"
                        else "x5"
                    val boostColor =
                        if (item.type == GameViewModel.ItemType.BOOST_X2)
                            Color(0xFF4CAF50)
                        else Color(0xFFFF9800)

                    Box(
                        modifier =
                            Modifier
                                    .size(50.dp)
                                    .graphicsLayer {
                                            translationX = offsetX
                                            translationY = offsetY
                                    }
                                    .clip(RoundedCornerShape(25.dp))
                                    .background(boostColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = boostText,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }

        // Render boost timer
        activeBoost?.let { boost ->
            val seconds = (boost.remainingMs / 1000).toInt()
            val boostColor =
                if (boost.multiplier == 2) Color(0xFF4CAF50) else Color(0xFFFF9800)

            Box(
                modifier =
                    Modifier
                            .offset(y = (-150).dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(boostColor)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text =
                        "0:${seconds.toString().padStart(2, '0')} • x${boost.multiplier}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

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
                modifier =
                    Modifier
                            .size(PinnedCarrotSize)
                            .graphicsLayer {
                                    translationX = offsetX
                                    translationY = offsetY
                                    rotationZ = totalAngle + 270f
                            },
            )
        }
    }
}

@Composable
private fun ThrowingCarrot(
    triggerKey: Int,
    isActive: Boolean,
    isBouncing: Boolean,
    onFlightFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (triggerKey == 0 || !isActive) return

    val density = LocalDensity.current
    val progress = remember(triggerKey) { Animatable(0f) }


    val rabbitPosition = remember(density) { Offset(0f, with(density) { 450.dp.toPx() }) }


    val basketRimPosition =
        remember(density) {
            val radiusPx = with(density) { (BasketSize / 2).toPx() }
            val radians = Math.toRadians(TARGET_ANGLE.toDouble())
            Offset(
                x = (cos(radians) * radiusPx).toFloat(),
                y = (sin(radians) * radiusPx).toFloat()
            )
        }


    val carrotRotation = TARGET_ANGLE + 270f

    LaunchedEffect(triggerKey, isBouncing) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec =
                tween(
                    durationMillis = CARROT_FLIGHT_DURATION_MS.toInt(),
                    easing = LinearEasing
                )
        )
        onFlightFinished()
    }

    val currentPosition =
        if (isBouncing) {
            val bounceDownPosition = Offset(0f, with(density) { 600.dp.toPx() })
            Offset(
                x =
                    androidx.compose.ui.util.lerp(
                        basketRimPosition.x,
                        bounceDownPosition.x,
                        progress.value
                    ),
                y =
                    androidx.compose.ui.util.lerp(
                        basketRimPosition.y,
                        bounceDownPosition.y,
                        progress.value
                    )
            )
        } else {
            Offset(
                x =
                    androidx.compose.ui.util.lerp(
                        rabbitPosition.x,
                        basketRimPosition.x,
                        progress.value
                    ),
                y =
                    androidx.compose.ui.util.lerp(
                        rabbitPosition.y,
                        basketRimPosition.y,
                        progress.value
                    )
            )
        }

    Image(
        painter = painterResource(id = R.drawable.carrot),
        contentDescription = null,
        modifier =
            modifier
                    .size(ThrowingCarrotSize)
                    .graphicsLayer {
                            translationX = currentPosition.x
                            translationY = currentPosition.y
                            rotationZ = carrotRotation
                    },
        contentScale = ContentScale.Fit
    )
}
