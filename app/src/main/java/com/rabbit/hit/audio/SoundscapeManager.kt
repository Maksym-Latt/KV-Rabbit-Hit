package com.rabbit.hit.audio

interface SoundscapeManager {
    fun launchMenuTheme()
    fun launchSessionTheme()
    fun muteAllThemes()
    fun freezeTheme()
    fun unfreezeTheme()

    fun updateMusicLevel(percent: Int)
    fun updateEffectsLevel(percent: Int)
    fun toggleHaptics(enabled: Boolean)

    fun emitDefeatCue()
    fun emitRewardCue()
    fun emitImpactCue()
    fun emitSuccessCue()
}
