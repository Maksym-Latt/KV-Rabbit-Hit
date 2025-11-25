package com.rabbit.hit.ui.main.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rabbit.hit.audio.AudioController
import com.rabbit.hit.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository,
    private val audio: AudioController
) : ViewModel() {

    private val _ui = MutableStateFlow(SettingsUiState())
    val ui: StateFlow<SettingsUiState> = _ui

    init {
        val music = repo.getMusicVolume()
        val sound = repo.getSoundVolume()
        val vibration = repo.isVibrationEnabled()
        _ui.value = SettingsUiState(
            musicVolume = music,
            soundVolume = sound,
            vibrationEnabled = vibration
        )
        audio.setMusicVolume(music)
        audio.setSoundVolume(sound)
        audio.setVibrationEnabled(vibration)
    }

    fun setMusicVolume(value: Int) {
        val v = value.coerceIn(0, 100)
        _ui.value = _ui.value.copy(musicVolume = v)
        viewModelScope.launch { repo.setMusicVolume(v) }
        audio.setMusicVolume(v)
    }

    fun setSoundVolume(value: Int) {
        val v = value.coerceIn(0, 100)
        _ui.value = _ui.value.copy(soundVolume = v)
        viewModelScope.launch { repo.setSoundVolume(v) }
        audio.setSoundVolume(v)
    }

    fun toggleMusic(enabled: Boolean) {
        setMusicVolume(if (enabled) 70 else 0)
    }

    fun toggleSound(enabled: Boolean) {
        setSoundVolume(if (enabled) 80 else 0)
    }

    fun toggleVibration(enabled: Boolean) {
        _ui.value = _ui.value.copy(vibrationEnabled = enabled)
        viewModelScope.launch { repo.setVibrationEnabled(enabled) }
        audio.setVibrationEnabled(enabled)
    }
}
