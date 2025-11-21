package com.rabbit.hit.ui.main.menuscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rabbit.hit.data.progress.PlayerProgressRepository
import com.rabbit.hit.ui.main.gamescreen.GameResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val progressRepository: PlayerProgressRepository,
) : ViewModel() {

    enum class Screen { Menu, Game }

    data class UiState(
        val screen: Screen = Screen.Menu,
        val lastHeight: Int = 0,
        val coins: Int = 0,
        val level: Int = 1,
        val selectedSkin: EggSkin = EggSkin.Classic,
        val ownedSkins: Set<EggSkin> = setOf(EggSkin.Classic)
    )

    private val _ui = MutableStateFlow(
        UiState(
            lastHeight = progressRepository.progress.value.bestHeight,
            coins = progressRepository.progress.value.coins,
            level = progressRepository.progress.value.level,
            selectedSkin = progressRepository.progress.value.selectedSkin,
            ownedSkins = progressRepository.progress.value.ownedSkins,
        )
    )
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    val skins: List<EggSkin> = EggSkin.entries

    init {
        viewModelScope.launch {
            progressRepository.progress.collect { progress ->
                _ui.update {
                    it.copy(
                        coins = progress.coins,
                        lastHeight = progress.bestHeight,
                        selectedSkin = progress.selectedSkin,
                        ownedSkins = progress.ownedSkins,
                        level = progress.level
                    )
                }
            }
        }
    }

    fun startGame() {
        _ui.update { it.copy(screen = Screen.Game) }
    }

    fun backToMenu(_result: GameResult) {
        _ui.update { it.copy(screen = Screen.Menu) }
    }

    fun backToMenu() {
        _ui.update { it.copy(screen = Screen.Menu) }
    }

    fun selectSkin(skin: EggSkin) {
        progressRepository.selectSkin(skin)
    }

    fun buySkin(skin: EggSkin) {
        progressRepository.buySkin(skin)
    }
}
