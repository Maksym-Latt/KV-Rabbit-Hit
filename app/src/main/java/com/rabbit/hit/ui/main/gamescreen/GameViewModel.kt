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

private const val TARGET_ANGLE = 90f
private const val COLLISION_THRESHOLD = 12f
private const val ROTATION_ACCELERATION = 0.25f

@HiltViewModel
class GameViewModel @Inject constructor(
    private val progressRepository: PlayerProgressRepository,
) : ViewModel() {

    data class CarrotPin(val angle: Float)

    data class GameUiState(
        val running: Boolean = false,
        val showIntro: Boolean = true,
        val isPaused: Boolean = false,
        val isGameOver: Boolean = false,
        val score: Int = 0,
        val multiplier: Int = 1,
        val coins: Int = 0,
        val basketAngle: Float = 0f,
        val rotationSpeed: Float = 40f,
        val carrots: List<CarrotPin> = emptyList(),
        val skin: RabbitSkin = RabbitSkin.Classic,
        val throwId: Int = 0,
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
                coins = 0,
            )
        }
    }

    fun tick(deltaMs: Long = 16L) {
        val deltaSeconds = deltaMs / 1000f
        _state.update { current ->
            if (!current.running || current.isPaused || current.isGameOver) return@update current
            val newAngle = (current.basketAngle + current.rotationSpeed * deltaSeconds) % 360f
            val newSpeed = current.rotationSpeed + (ROTATION_ACCELERATION * deltaSeconds)
            current.copy(
                basketAngle = newAngle,
                rotationSpeed = newSpeed,
            )
        }
    }

    fun throwCarrot() {
        val current = _state.value
        if (!current.running || current.isPaused || current.isGameOver) return

        val target = TARGET_ANGLE
        val hasCollision = current.carrots.any { pin ->
            val worldAngle = normalizeAngle(pin.angle + current.basketAngle)
            angleDistance(worldAngle, target) < COLLISION_THRESHOLD
        }

        if (hasCollision) {
            _state.update { it.copy(throwId = it.throwId + 1) }
            gameOver()
            return
        }

        val newPinAngle = normalizeAngle(target - current.basketAngle)
        val gainedMultiplier = when {
            current.score >= 30 -> 5
            current.score >= 12 -> 2
            else -> 1
        }
        val newMultiplier = maxOf(current.multiplier, gainedMultiplier)
        val updatedScore = current.score + newMultiplier
        val updatedCoins = current.coins + if (updatedScore % 5 == 0) newMultiplier else 0

        _state.update {
            it.copy(
                carrots = it.carrots + CarrotPin(newPinAngle),
                score = updatedScore,
                multiplier = newMultiplier,
                rotationSpeed = it.rotationSpeed + 2f,
                coins = updatedCoins,
                throwId = it.throwId + 1,
            )
        }
        emitEvent(GameEvent.CoinCollected)
    }

    private fun gameOver() {
        _state.update {
            it.copy(
                running = false,
                isGameOver = true,
                isPaused = false,
            )
        }
        emitEvent(GameEvent.GameOver)
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
