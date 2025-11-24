package com.rabbit.hit.ui.main.menuscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rabbit.hit.data.progress.PlayerProgressRepository
import com.rabbit.hit.data.progress.RabbitSkin
import com.rabbit.hit.ui.main.gamescreen.GameResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val progressRepository: PlayerProgressRepository,
) : ViewModel() {

    enum class Screen { Menu, Game }

    data class UiState(
        val screen: Screen = Screen.Menu,
        val bestScore: Int = 0,
        val coins: Int = 0,
        val selectedSkin: RabbitSkin = RabbitSkin.Classic,
        val ownedSkins: Set<RabbitSkin> = setOf(RabbitSkin.Classic)
    )

    private val _ui = MutableStateFlow(
        UiState(
            bestScore = progressRepository.progress.value.bestScore,
            coins = progressRepository.progress.value.coins,
            selectedSkin = progressRepository.progress.value.selectedSkin,
            ownedSkins = progressRepository.progress.value.ownedSkins,
        )
    )
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    val skins: List<RabbitSkin> = RabbitSkin.entries

    init {
        viewModelScope.launch {
            progressRepository.progress.collect { progress ->
                _ui.update {
                    it.copy(
                        coins = progress.coins,
                        bestScore = progress.bestScore,
                        selectedSkin = progress.selectedSkin,
                        ownedSkins = progress.ownedSkins,
                    )
                }
            }
        }
    }

    fun startGame() {
        _ui.update { it.copy(screen = Screen.Game) }
    }

    fun backToMenu(result: GameResult? = null) {
        result?.let { progressRepository.recordFinishedRun(score = it.score, coinsEarned = it.coins) }
        _ui.update { it.copy(screen = Screen.Menu) }
    }

    fun selectSkin(skin: RabbitSkin) {
        progressRepository.selectSkin(skin)
    }

    fun buySkin(skin: RabbitSkin): Boolean = progressRepository.buySkin(skin)
}
