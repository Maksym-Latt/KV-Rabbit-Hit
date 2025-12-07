package com.rabbit.hit.data.settings

interface SettingsRepository {
    fun fetchMusicLevel(): Int
    fun fetchEffectsLevel(): Int
    fun isHapticsActive(): Boolean

    fun updateMusicLevel(value: Int)
    fun updateEffectsLevel(value: Int)
    fun updateHapticsState(enabled: Boolean)
}