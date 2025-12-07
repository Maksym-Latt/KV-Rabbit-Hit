package com.rabbit.hit.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import com.rabbit.hit.data.settings.SettingsRepository

@Singleton
class DefaultSoundscapeManager @Inject constructor(
    @ApplicationContext private val context: Context,
    settingsRepo: SettingsRepository
) : SoundscapeManager {

    private enum class AtmosLayer { LOBBY, SESSION }
    private enum class CueMarker { TRIUMPH, FAILURE, STRIKE }

    private val cuePool: SoundPool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val cueAliases = mapOf(
        CueMarker.TRIUMPH to "sfx_win",
        CueMarker.FAILURE to "sfx_lose",
        CueMarker.STRIKE to "sfx_hit",
    )

    private val cueToResId = cueAliases.mapValues { resolveRaw(it.value) }
    private val cueHandleMap = mutableMapOf<CueMarker, Int>()
    private val preparedHandles = mutableSetOf<Int>()
    private val delayedTriggers = mutableSetOf<Int>()

    private val lobbyThemeRes = resolveRaw("music_menu")
    private val sessionThemeRes = resolveRaw("music_game")

    private val layerPlayers = mutableMapOf<AtmosLayer, MediaPlayer>()
    private var activeLayer: AtmosLayer? = null

    private var bgmLevel: Float = settingsRepo.fetchMusicLevel().toVolume()
    private var cueLevel: Float = settingsRepo.fetchEffectsLevel().toVolume()
    private var hapticActive: Boolean = settingsRepo.isHapticsActive()
    private var wasSuspendedDueToLifecycle: Boolean = false

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    init {
        cuePool.setOnLoadCompleteListener { pool, sampleId, status ->
            if (status == 0) {
                preparedHandles += sampleId
                if (delayedTriggers.remove(sampleId)) {
                    pool.play(sampleId, cueLevel, cueLevel, 1, 0, 1f)
                }
            } else {
                delayedTriggers.remove(sampleId)
            }
        }
    }

    override fun launchMenuTheme() {
        wasSuspendedDueToLifecycle = false
        playMusic(AtmosLayer.LOBBY)
    }

    override fun launchSessionTheme() {
        wasSuspendedDueToLifecycle = false
        playMusic(AtmosLayer.SESSION)
    }

    override fun muteAllThemes() {
        activeLayer?.let { channel ->
            layerPlayers[channel]?.let { player ->
                if (player.isPlaying) player.pause()
                player.seekTo(0)
            }
        }
        activeLayer = null
        wasSuspendedDueToLifecycle = false
    }

    override fun freezeTheme() {
        activeLayer?.let { channel ->
            layerPlayers[channel]?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                    wasSuspendedDueToLifecycle = true
                }
            }
        }
    }

    override fun unfreezeTheme() {
        if (!wasSuspendedDueToLifecycle) return
        wasSuspendedDueToLifecycle = false
        activeLayer?.let { channel ->
            val player = layerPlayers[channel]
            if (player != null && !player.isPlaying) {
                try {
                    player.start()
                } catch (_: IllegalStateException) {
                    layerPlayers.remove(channel)
                    activeLayer = null
                }
            } else if (player == null) {
                activeLayer = null
            }
        }
    }

    override fun updateMusicLevel(percent: Int) {
        bgmLevel = percent.toVolume()
        layerPlayers.values.forEach { player ->
            player.setVolume(bgmLevel, bgmLevel)
        }
    }

    override fun updateEffectsLevel(percent: Int) {
        cueLevel = percent.toVolume()
    }

    override fun toggleHaptics(enabled: Boolean) {
        hapticActive = enabled
    }

    override fun emitDefeatCue() {
        playEffect(CueMarker.FAILURE)
        vibrate(160L)
    }

    override fun emitRewardCue() {

    }

    override fun emitImpactCue() {
        playEffect(CueMarker.STRIKE)
    }

    override fun emitSuccessCue() {
        playEffect(CueMarker.TRIUMPH)
        vibrate(200L)
    }


    private fun playMusic(channel: AtmosLayer) {
        if (activeLayer == channel && layerPlayers[channel]?.isPlaying == true) {
            return
        }

        activeLayer?.let { active ->
            layerPlayers[active]?.let { player ->
                if (player.isPlaying) player.pause()
                player.seekTo(0)
            }
        }

        val player = ensurePlayer(channel)
        activeLayer = if (player != null) channel else null
        player?.let {
            it.setVolume(bgmLevel, bgmLevel)
            it.isLooping = true
            try {
                it.start()
            } catch (_: IllegalStateException) {
                // player might have been released by the system, recreate lazily on next call
                layerPlayers.remove(channel)
            }
        }
    }

    private fun ensurePlayer(channel: AtmosLayer): MediaPlayer? {
        layerPlayers[channel]?.let { existing ->
            return existing
        }

        val resId = when (channel) {
            AtmosLayer.LOBBY -> lobbyThemeRes
            AtmosLayer.SESSION -> sessionThemeRes
        }

        if (resId == 0) return null

        return runCatching { MediaPlayer.create(context, resId) }
            .getOrNull()
            ?.also { player ->
                player.isLooping = true
                player.setVolume(bgmLevel, bgmLevel)
                layerPlayers[channel] = player
            }
    }

    private fun playEffect(effect: CueMarker) {
        if (cueLevel <= 0f) return

        val resId = cueToResId[effect] ?: 0
        if (resId == 0) return

        val sampleId = cueHandleMap[effect]
        if (sampleId != null) {
            if (preparedHandles.contains(sampleId)) {
                cuePool.play(sampleId, cueLevel, cueLevel, 1, 0, 1f)
            } else {
                delayedTriggers += sampleId
            }
            return
        }

        val loadId = cuePool.load(context, resId, 1)
        if (loadId != 0) {
            cueHandleMap[effect] = loadId
            delayedTriggers += loadId
        }
    }

    private fun resolveRaw(name: String): Int {
        if (name.isBlank()) return 0
        return context.resources.getIdentifier(name, "raw", context.packageName)
    }

    private fun Int.toVolume(): Float = (this.coerceIn(0, 100) / 100f).coerceIn(0f, 1f)

    private fun vibrate(durationMs: Long) {

    }
}
