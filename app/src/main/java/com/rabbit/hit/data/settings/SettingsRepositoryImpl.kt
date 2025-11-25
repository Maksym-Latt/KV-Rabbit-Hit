package com.rabbit.hit.data.settings

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val prefs: SharedPreferences
) : SettingsRepository {

    private companion object {
        const val KEY_MUSIC = "music_volume"
        const val KEY_SOUND = "sound_volume"
        const val KEY_VIBRATION = "vibration_enabled"
        const val DEF_MUSIC = 70
        const val DEF_SOUND = 80
        const val DEF_VIBRATION = true
    }

    override fun getMusicVolume(): Int = prefs.getInt(KEY_MUSIC, DEF_MUSIC)
    override fun getSoundVolume(): Int = prefs.getInt(KEY_SOUND, DEF_SOUND)
    override fun isVibrationEnabled(): Boolean = prefs.getBoolean(KEY_VIBRATION, DEF_VIBRATION)

    override fun setMusicVolume(value: Int) {
        prefs.edit().putInt(KEY_MUSIC, value.coerceIn(0, 100)).apply()
    }

    override fun setSoundVolume(value: Int) {
        prefs.edit().putInt(KEY_SOUND, value.coerceIn(0, 100)).apply()
    }

    override fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply()
    }
}