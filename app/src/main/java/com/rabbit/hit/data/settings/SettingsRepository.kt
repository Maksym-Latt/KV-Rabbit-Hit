package com.rabbit.hit.data.settings

interface SettingsRepository {
    fun getMusicVolume(): Int
    fun getSoundVolume(): Int
    fun isVibrationEnabled(): Boolean

    fun setMusicVolume(value: Int)
    fun setSoundVolume(value: Int)
    fun setVibrationEnabled(enabled: Boolean)
}