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
internal const val COLLISION_THRESHOLD = 5f
internal const val CARROT_FLIGHT_DURATION_MS = 110L
private const val ROTATION_ACCELERATION = 0.45f
private const val ROTATION_SPEED_LIMIT = 140f

@HiltViewModel
class GameViewModel @Inject constructor(
    private val progressRepository: PlayerProgressRepository,
) : ViewModel() {

    data class CarrotPin(val angle: Float)

    data class CarrotFlight(val id: Int, val elapsedMs: Long = 0)

    data class GameUiState(
        val running: Boolean = false,
        val showIntro: Boolean = true,
        val isPaused: Boolean = false,
        val isGameOver: Boolean = false,
        val score: Int = 0,
        val multiplier: Int = 1,
        val coins: Int = 0,
        val basketAngle: Float = 0f,
        val rotationSpeed: Float = 55f,
        val carrots: List<CarrotPin> = emptyList(),
        val skin: RabbitSkin = RabbitSkin.Classic,
        val throwId: Int = 0,
        val flight: CarrotFlight? = null,
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
        _state.update {
            GameUiState(
                running = true,
                showIntro = false,
                skin = it.skin,
            )
        }
    }

    fun tick(deltaMs: Long = 16L) {
        if (deltaMs <= 0) return
        val deltaSeconds = deltaMs / 1000f

        _state.update { current ->
            if (!current.running || current.isPaused || current.isGameOver) return@update current

            val newAngle = normalizeAngle(current.basketAngle + current.rotationSpeed * deltaSeconds)
            val newSpeed = (current.rotationSpeed + ROTATION_ACCELERATION * deltaSeconds)
                .coerceAtMost(ROTATION_SPEED_LIMIT)

            val flight = current.flight
            if (flight == null) {
                return@update current.copy(
                    basketAngle = newAngle,
                    rotationSpeed = newSpeed,
                )
            }

            val updatedFlight = flight.copy(elapsedMs = flight.elapsedMs + deltaMs)
            if (updatedFlight.elapsedMs >= CARROT_FLIGHT_DURATION_MS) {
                return@update resolveImpact(current.copy(basketAngle = newAngle, rotationSpeed = newSpeed))
            }

            current.copy(
                basketAngle = newAngle,
                rotationSpeed = newSpeed,
                flight = updatedFlight,
            )
        }
    }

    fun throwCarrot() {
        val current = _state.value
        if (!current.running || current.isPaused || current.isGameOver) return
        if (current.flight != null) return

        _state.update {
            val nextId = it.throwId + 1
            it.copy(
                throwId = nextId,
                flight = CarrotFlight(id = nextId, elapsedMs = 0),
            )
        }
    }

    fun resolveFlightIfReady() {
        _state.update { current ->
            if (current.flight == null || current.isPaused || !current.running || current.isGameOver) return@update current
            resolveImpact(current)
        }
    }

    private fun resolveImpact(current: GameUiState): GameUiState {
        val collision = current.carrots.any { pin ->
            val worldAngle = normalizeAngle(pin.angle + current.basketAngle)
            angleDistance(worldAngle, TARGET_ANGLE) < COLLISION_THRESHOLD
        }

        if (collision) {
            emitEvent(GameEvent.GameOver)
            return current.copy(
                running = false,
                isGameOver = true,
                isPaused = false,
                flight = null,
            )
        }

        val pinnedAngle = normalizeAngle(TARGET_ANGLE - current.basketAngle)
        val gainedMultiplier = when {
            current.score >= 40 -> 6
            current.score >= 24 -> 3
            current.score >= 12 -> 2
            else -> 1
        }
        val newMultiplier = maxOf(current.multiplier, gainedMultiplier)
        val updatedScore = current.score + newMultiplier
        val updatedCoins = current.coins + if (updatedScore % 5 == 0) newMultiplier else 0

        emitEvent(GameEvent.CoinCollected)

        return current.copy(
            carrots = current.carrots + CarrotPin(pinnedAngle),
            score = updatedScore,
            coins = updatedCoins,
            multiplier = newMultiplier,
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
        _state.update {
            GameUiState(
                running = true,
                showIntro = false,
                skin = it.skin,
            )
        }
    }

    fun currentResult(): GameResult = GameResult(score = _state.value.score, coins = _state.value.coins)

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
}

private fun angleDistance(a: Float, b: Float): Float {
    val diff = abs(normalizeAngle(a) - normalizeAngle(b)) % 360f
    return if (diff > 180f) 360f - diff else diff
}

private fun normalizeAngle(angle: Float): Float = (angle % 360f + 360f) % 360f
