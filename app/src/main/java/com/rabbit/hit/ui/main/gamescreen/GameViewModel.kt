package com.rabbit.hit.ui.main.gamescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rabbit.hit.data.progress.PlayerProgressRepository
import com.rabbit.hit.data.progress.RabbitSkin
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.abs
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal const val TARGET_ANGLE = 90f
internal const val COLLISION_THRESHOLD = 8f
internal const val CARROT_FLIGHT_DURATION_MS = 80L
internal const val CARROT_BOUNCE_DURATION_MS = 420L
private const val ROTATION_ACCELERATION = 0.45f
private const val ROTATION_SPEED_LIMIT = 140f
private const val MIN_ROTATION_SPEED = 25f
private val BASE_TARGET_SCORE = calculateTargetScore()
private const val BOOST_DURATION_MS = 5_000L

@HiltViewModel
class GameViewModel
@Inject
constructor(
        private val progressRepository: PlayerProgressRepository,
) : ViewModel() {

    data class CarrotPin(val angle: Float)
    data class StickPin(val angle: Float)

    enum class ItemType {
        COIN,
        BOOST_X2,
        BOOST_X5
    }

    data class OrbitingItem(
            val angle: Float,
            val type: ItemType,
            val id: Int = (Math.random() * 100000).toInt()
    )

    data class ActiveBoost(
            val multiplier: Int,
            val remainingMs: Long,
            val totalDurationMs: Long,
    )

    data class CarrotFlight(val id: Int, val elapsedMs: Long = 0, val bouncing: Boolean = false)

    data class GameUiState(
            val running: Boolean = false,
            val showIntro: Boolean = true,
            val isPaused: Boolean = false,
            val isGameOver: Boolean = false,
            val isWin: Boolean = false,
            val score: Int = 0,
            val multiplier: Int = 1,
            val coins: Int = 0,
            val targetScore: Int = BASE_TARGET_SCORE,
            val basketAngle: Float = 0f,
            val rotationSpeed: Float = 55f,
            val targetRotationSpeed: Float = 55f,
            val carrots: List<CarrotPin> = emptyList(),
            val sticks: List<StickPin> = emptyList(),
            val orbitingItems: List<OrbitingItem> = emptyList(),
            val activeBoost: ActiveBoost? = null,
            val skin: RabbitSkin = RabbitSkin.Classic,
            val throwId: Int = 0,
            val flight: CarrotFlight? = null,
            val lastSpeedChangeMs: Long = 0,
    )

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state

    private val _events = MutableSharedFlow<GameEvent>()
    val events: SharedFlow<GameEvent> = _events.asSharedFlow()

    fun setSkin(skin: RabbitSkin) {
        _state.update { it.copy(skin = skin) }
    }

    fun showIntroOnEnter() {
        _state.update { GameUiState(skin = it.skin, showIntro = true) }
    }

    fun startRun() {
        val orbitingItems = mutableListOf<OrbitingItem>()
        val sticks = generateSticks()

        val targetScore = (BASE_TARGET_SCORE - (sticks.size * 3)).coerceAtLeast(1)

        // Add 2-3 coins at random angles
        val coinAngles = listOf(30f, 120f, 210f, 300f).shuffled().take((2..3).random())
        coinAngles.forEach { angle -> orbitingItems.add(OrbitingItem(angle, ItemType.COIN)) }

        // Add a single boost item with a 50% chance
        if ((0..1).random() == 1) {
            val boostType = if ((0..1).random() == 0) ItemType.BOOST_X2 else ItemType.BOOST_X5
            val boostAngle = listOf(60f, 240f).random()
            orbitingItems.add(OrbitingItem(boostAngle, boostType))
        }

        _state.update {
            GameUiState(
                    running = true,
                    showIntro = false,
                    skin = it.skin,
                    orbitingItems = orbitingItems,
                    sticks = sticks,
                    targetRotationSpeed = 55f,
                    targetScore = targetScore,
            )
        }
    }

    fun tick(deltaMs: Long = 16L) {
        if (deltaMs <= 0) return
        val deltaSeconds = deltaMs / 1000f

        _state.update { current ->
            if (!current.running || current.isPaused || current.isGameOver) return@update current

            // Update boost timer
            val updatedBoost =
                    current.activeBoost?.let { boost ->
                        val remaining = boost.remainingMs - deltaMs
                        if (remaining <= 0) null else boost.copy(remainingMs = remaining)
                    }

            // Dynamic speed variation - basket rotates 60%+ of time
            // Pauses are rare (10%) and max 1 second
            val timeSinceLastChange = System.currentTimeMillis() - current.lastSpeedChangeMs
            val shouldChangeSpeed = timeSinceLastChange > (1500..3000).random()

            val newTargetSpeed =
                    if (shouldChangeSpeed) {
                        when ((0..100).random()) {
                            in 0..10 -> MIN_ROTATION_SPEED // Pause replaced with slow spin (10%)
                            in 11..25 -> -current.rotationSpeed * 0.7f // Reverse (15%)
                            in 26..40 -> current.rotationSpeed * 1.6f // Speed up (15%)
                            else -> 55f // Normal rotation (60%)
                        }
                    } else {
                        current.targetRotationSpeed
                    }
                            .let(::enforceMinimumRotationSpeed)
                            .coerceIn(-ROTATION_SPEED_LIMIT, ROTATION_SPEED_LIMIT)

            // Smooth speed interpolation
            val smoothedSpeed =
                    current.rotationSpeed + (newTargetSpeed - current.rotationSpeed) * 0.05f

            val newAngle = normalizeAngle(current.basketAngle + smoothedSpeed * deltaSeconds)

            val flight = current.flight
            if (flight == null) {
                return@update current.copy(
                        basketAngle = newAngle,
                        rotationSpeed = smoothedSpeed,
                        targetRotationSpeed = newTargetSpeed,
                        activeBoost = updatedBoost,
                        lastSpeedChangeMs =
                                if (shouldChangeSpeed) System.currentTimeMillis()
                                else current.lastSpeedChangeMs
                )
            }

            // Handle bouncing carrot
            if (flight.bouncing) {
                if (flight.elapsedMs >= CARROT_BOUNCE_DURATION_MS) {
                    // Bounce finished, game over
                    emitEvent(GameEvent.GameOver)
                    return@update current.copy(
                            running = false,
                            isGameOver = true,
                            isPaused = false,
                            flight = null,
                    )
                }
                val updatedFlight = flight.copy(elapsedMs = flight.elapsedMs + deltaMs)
                return@update current.copy(
                        basketAngle = newAngle,
                        rotationSpeed = smoothedSpeed,
                        targetRotationSpeed = newTargetSpeed,
                        flight = updatedFlight,
                        activeBoost = updatedBoost,
                )
            }

            val updatedFlight = flight.copy(elapsedMs = flight.elapsedMs + deltaMs)
            if (updatedFlight.elapsedMs >= CARROT_FLIGHT_DURATION_MS) {
                return@update resolveImpact(
                        current.copy(
                                basketAngle = newAngle,
                                rotationSpeed = smoothedSpeed,
                                targetRotationSpeed = newTargetSpeed,
                                activeBoost = updatedBoost,
                        )
                )
            }

            current.copy(
                    basketAngle = newAngle,
                    rotationSpeed = smoothedSpeed,
                    targetRotationSpeed = newTargetSpeed,
                    flight = updatedFlight,
                    activeBoost = updatedBoost,
            )
        }
    }

    fun throwCarrot(): Boolean {
        val current = _state.value
        if (!current.running || current.isPaused || current.isGameOver) return false
        if (current.flight != null) return false

        _state.update {
            val nextId = it.throwId + 1
            it.copy(
                    throwId = nextId,
                    flight = CarrotFlight(id = nextId, elapsedMs = 0),
            )
        }
        return true
    }

    fun resolveFlightIfReady() {
        _state.update { current ->
            if (
                current.flight == null ||
                current.flight.bouncing ||
                current.isPaused ||
                !current.running ||
                current.isGameOver
            )
                    return@update current
            resolveImpact(current)
        }
    }

    private fun resolveImpact(current: GameUiState): GameUiState {
        // Check collision with orbiting items first (coins and boosts)
        val hitItem =
                current.orbitingItems.firstOrNull { item ->
                    val worldAngle = normalizeAngle(item.angle + current.basketAngle)
                    angleDistance(worldAngle, TARGET_ANGLE) < COLLISION_THRESHOLD * 2
                }

        if (hitItem != null) {
            // Pin the carrot to rim even when collecting items
            val pinnedAngle = normalizeAngle(TARGET_ANGLE - current.basketAngle)

            return when (hitItem.type) {
                ItemType.COIN -> {
                    // Collect coin and pin carrot
                    emitEvent(GameEvent.CoinCollected)
                    current.copy(
                            coins = current.coins + 5,
                            orbitingItems = current.orbitingItems.filter { it.id != hitItem.id },
                            carrots = current.carrots + CarrotPin(pinnedAngle),
                            flight = null,
                    )
                }
                ItemType.BOOST_X2, ItemType.BOOST_X5 -> {
                    // Activate boost and pin carrot
                    val boostMultiplier = if (hitItem.type == ItemType.BOOST_X2) 2 else 5
                    emitEvent(GameEvent.BoostCollected)
                    current.copy(
                            activeBoost =
                                    ActiveBoost(
                                            boostMultiplier,
                                            BOOST_DURATION_MS,
                                            BOOST_DURATION_MS,
                                    ),
                            orbitingItems = current.orbitingItems.filter { it.id != hitItem.id },
                            carrots = current.carrots + CarrotPin(pinnedAngle),
                            flight = null,
                    )
                }
            }
        }

        // Check collision with pinned carrots or sticks
        val carrotCollision =
                current.carrots.any { pin ->
                    val worldAngle = normalizeAngle(pin.angle + current.basketAngle)
                    angleDistance(worldAngle, TARGET_ANGLE) < COLLISION_THRESHOLD
                }

        val stickCollision =
                current.sticks.any { stick ->
                    val worldAngle = normalizeAngle(stick.angle + current.basketAngle)
                    angleDistance(worldAngle, TARGET_ANGLE) < COLLISION_THRESHOLD
                }

        if (carrotCollision || stickCollision) {
            // Bounce back
            return current.copy(
                    flight = current.flight?.copy(bouncing = true, elapsedMs = 0),
            )
        }

        // No collision - pin the carrot to rim
        val pinnedAngle = normalizeAngle(TARGET_ANGLE - current.basketAngle)
        val gainedMultiplier =
                when {
                    current.score >= 40 -> 6
                    current.score >= 24 -> 3
                    current.score >= 12 -> 2
                    else -> 1
                }

        val activeMultiplier = current.activeBoost?.multiplier ?: 1
        val totalMultiplier = maxOf(current.multiplier, gainedMultiplier) * activeMultiplier
        val updatedScore = current.score + totalMultiplier
        val updatedCoins = current.coins + if (updatedScore % 5 == 0) totalMultiplier else 0

        emitEvent(GameEvent.CoinCollected)

        // Check for win condition
        if (updatedScore >= current.targetScore) {
            emitEvent(GameEvent.GameWin)
            return current.copy(
                    carrots = current.carrots + CarrotPin(pinnedAngle),
                    score = updatedScore,
                    coins = updatedCoins,
                    multiplier = maxOf(current.multiplier, gainedMultiplier),
                    running = false,
                    isGameOver = true,
                    isWin = true,
                    isPaused = false,
                    flight = null,
            )
        }

        return current.copy(
                carrots = current.carrots + CarrotPin(pinnedAngle),
                score = updatedScore,
                coins = updatedCoins,
                multiplier = maxOf(current.multiplier, gainedMultiplier),
                rotationSpeed = (current.rotationSpeed + 3f).coerceAtMost(ROTATION_SPEED_LIMIT),
                flight = null,
        )
    }

    fun pause() {
        _state.update { if (it.running) it.copy(isPaused = true) else it }
    }

    fun resume() {
        _state.update { it.copy(isPaused = false) }
    }

    fun retry() {
        val orbitingItems = mutableListOf<OrbitingItem>()
        val sticks = generateSticks()

        val targetScore = (BASE_TARGET_SCORE - sticks.size * 2).coerceAtLeast(1)

        val coinAngles = listOf(30f, 120f, 210f, 300f).shuffled().take((2..3).random())
        coinAngles.forEach { angle -> orbitingItems.add(OrbitingItem(angle, ItemType.COIN)) }

        if ((0..1).random() == 1) {
            val boostType = if ((0..1).random() == 0) ItemType.BOOST_X2 else ItemType.BOOST_X5
            val boostAngle = listOf(60f, 240f).random()
            orbitingItems.add(OrbitingItem(boostAngle, boostType))
        }

        _state.update {
            GameUiState(
                    running = true,
                    showIntro = false,
                    skin = it.skin,
                    orbitingItems = orbitingItems,
                    sticks = sticks,
                    targetRotationSpeed = 55f,
                    targetScore = targetScore,
            )
        }
    }

    fun currentResult(): GameResult =
            GameResult(score = _state.value.score, coins = _state.value.coins)

    fun consumeResult() {
        viewModelScope.launch {
            val result = currentResult()
            progressRepository.recordFinishedRun(result.score, result.coins)
        }
    }

    private fun emitEvent(event: GameEvent) {
        viewModelScope.launch { _events.emit(event) }
    }
}

sealed interface GameEvent {
    data object CoinCollected : GameEvent
    data object GameOver : GameEvent
    data object GameWin : GameEvent
    data object BoostCollected : GameEvent
}

private fun angleDistance(a: Float, b: Float): Float {
    val diff = abs(normalizeAngle(a) - normalizeAngle(b)) % 360f
    return if (diff > 180f) 360f - diff else diff
}

private fun normalizeAngle(angle: Float): Float = (angle % 360f + 360f) % 360f

private fun enforceMinimumRotationSpeed(speed: Float): Float {
    if (speed == 0f) return 0f
    return if (speed > 0) speed.coerceAtLeast(MIN_ROTATION_SPEED)
    else speed.coerceAtMost(-MIN_ROTATION_SPEED)
}

private fun generateSticks(): List<GameViewModel.StickPin> {
    val stickCount = (0..3).random()
    if (stickCount == 0) return emptyList()

    val angles = mutableListOf<Float>()

    repeat(stickCount) {
        var angle: Float
        var attempts = 0
        do {
            angle = (0..359).random().toFloat()
            attempts++
        } while (angles.any { angleDistance(it, angle) < COLLISION_THRESHOLD * 1.5 } && attempts < 10)
        angles.add(angle)
    }

    return angles.map(GameViewModel::StickPin)
}

private fun calculateTargetScore(): Int {
    val idealCarrots = (360f / (COLLISION_THRESHOLD)).toInt()
    return (idealCarrots * 0.75f).toInt().coerceAtLeast(1)
}
