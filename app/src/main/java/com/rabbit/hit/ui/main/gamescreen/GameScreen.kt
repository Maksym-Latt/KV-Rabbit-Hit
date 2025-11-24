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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.rabbit.hit.ui.main.component.GradientOutlinedText
import com.rabbit.hit.ui.main.component.MenuCoinDisplay
import com.rabbit.hit.ui.main.component.MenuIconButton
import com.rabbit.hit.ui.main.component.SeymourFont
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
            activeBoost = state.activeBoost,
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
    activeBoost: GameViewModel.ActiveBoost?,
    onPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.displayCutout)
                    .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MenuCoinDisplay(
                amount = coins,
                onClick = {},
                modifier = Modifier.weight(1f, fill = false)
            )

            GameScoreBadge(
                score = score,
                targetScore = targetScore,
                multiplier = multiplier,
                modifier = Modifier.weight(1f)
            )

            Box(modifier = Modifier.weight(1f, fill = false), contentAlignment = Alignment.CenterEnd) {
                MenuIconButton(iconVector = Icons.Default.Pause, onClick = onPause)
            }
        }

        activeBoost?.let { boost ->
            Spacer(modifier = Modifier.height(8.dp))
            ActiveBoostBar(boost = boost)
        }
    }
}

@Composable
private fun GameScoreBadge(
    score: Int,
    targetScore: Int,
    multiplier: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier =
            modifier
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(22.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GradientOutlinedText(
                text = "${score}",
                fontSize = 36.sp,
                strokeWidth = 6f,
                strokeColor = Color.White,
                gradientColors = listOf(Color(0xffff872a), Color(0xffff872a))
            )
        }
    }
}

@Composable
private fun ActiveBoostBar(boost: GameViewModel.ActiveBoost) {
    val progress =
        (boost.remainingMs.toFloat() / boost.totalDurationMs.toFloat()).coerceIn(0f, 1f)
    val boostColor = if (boost.multiplier == 2) Color(0xFF4CAF50) else Color(0xFFFF9800)

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier =
                Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(boostColor.copy(alpha = 0.38f))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "x${boost.multiplier}",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier =
                        Modifier
                                .height(8.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.35f))
                ) {
                    Box(
                        modifier =
                            Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(progress)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                            Brush.horizontalGradient(
                                                    colors = listOf(
                                                        boostColor,
                                                        boostColor.copy(alpha = 0.6f)
                                                    )
                                            )
                                    )
                    )
                }
            }
        }
    }
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
                sticks = state.sticks,
                orbitingItems = state.orbitingItems,
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
            .size(width = 120.dp, height = 100.dp)) {
        Image(
            painter = painterResource(id = R.drawable.carrot),
            contentDescription = null,
            modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(86.dp)
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
                        .size(80.dp)
                        .rotate(12f),
            contentScale = ContentScale.Fit
        )
        Image(
            painter = painterResource(id = R.drawable.carrot),
            contentDescription = null,
            modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(82.dp)
                    .rotate(-24f),
            contentScale = ContentScale.Fit
        )
    }
}

private const val DEBUG_DRAW_HITBOXES = false
private val BasketSize = 260.dp
private val PinnedCarrotSize = 82.dp
private val ThrowingCarrotSize = 80.dp
private val StickSize = 92.dp

@Composable
private fun RotatingBasket(
    angle: Float,
    carrots: List<GameViewModel.CarrotPin>,
    sticks: List<GameViewModel.StickPin>,
    orbitingItems: List<GameViewModel.OrbitingItem>,
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
                        painter = painterResource(id = R.drawable.ic_coin),
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
                                    .size(42.dp)
                                    .graphicsLayer {
                                            translationX = offsetX
                                            translationY = offsetY
                                    }
                                    .clip(CircleShape)
                                    .background(boostColor.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.coin_placeholder),
                            contentDescription = null,
                            modifier = Modifier.matchParentSize(),
                            colorFilter = ColorFilter.tint(boostColor.copy(alpha = 0.8f))
                        )
                        Text(
                            text = boostText,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }
                }
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

                sticks.forEach { stick ->
                    val stickWorldAngle = stick.angle + angle
                    drawArc(
                        color = Color(0x805f4c00),
                        startAngle = stickWorldAngle - COLLISION_THRESHOLD,
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

        sticks.forEach { stick ->
            val totalAngle = stick.angle + angle
            val radians = Math.toRadians(totalAngle.toDouble())
            val offsetX = (cos(radians) * radiusPx).toFloat()
            val offsetY = (sin(radians) * radiusPx).toFloat()

            Image(
                painter = painterResource(id = R.drawable.stick),
                contentDescription = null,
                modifier =
                    Modifier
                            .size(StickSize)
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
