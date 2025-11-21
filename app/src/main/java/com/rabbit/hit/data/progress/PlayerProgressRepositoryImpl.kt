package com.rabbit.hit.data.progress

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class PlayerProgressRepositoryImpl @Inject constructor(
    private val prefs: SharedPreferences,
) : PlayerProgressRepository {

    companion object {
        private const val KEY_COINS = "progress_coins"
        private const val KEY_LEVEL = "progress_level"
        private const val KEY_BEST_HEIGHT = "progress_best_height"
        private const val KEY_SELECTED_SKIN = "progress_selected_skin"
        private const val KEY_OWNED_SKINS = "progress_owned_skins"
    }

    private val _progress = MutableStateFlow(loadFromPrefs())
    override val progress: StateFlow<PlayerProgress> = _progress.asStateFlow()

    override fun recordFinishedRun(coinsEarned: Int, height: Int, level: Int) {
        updateProgress { current ->
            val updatedCoins = (current.coins + coinsEarned).coerceAtLeast(0)
            val updatedBestHeight = maxOf(current.bestHeight, height)
            current.copy(
                coins = updatedCoins,
                bestHeight = updatedBestHeight,
                level = maxOf(current.level, level)
            )
        }
    }

    override fun saveLevel(level: Int) {
        updateProgress { current ->
            if (level == current.level) current else current.copy(level = level)
        }
    }

    override fun selectSkin(skin: EggSkin) {
        updateProgress { current ->
            if (current.ownedSkins.contains(skin)) current.copy(selectedSkin = skin) else current
        }
    }

    override fun buySkin(skin: EggSkin): Boolean {
        var purchased = false
        updateProgress { current ->
            if (current.ownedSkins.contains(skin)) return@updateProgress current
            if (current.coins < skin.price) return@updateProgress current
            purchased = true
            current.copy(
                coins = current.coins - skin.price,
                ownedSkins = current.ownedSkins + skin,
                selectedSkin = skin
            )
        }
        return purchased
    }

    private fun updateProgress(block: (PlayerProgress) -> PlayerProgress) {
        val updated = block(_progress.value)
        if (updated == _progress.value) return
        _progress.value = updated
        persist(updated)
    }

    private fun loadFromPrefs(): PlayerProgress {
        val coins = prefs.getInt(KEY_COINS, 0)
        val level = prefs.getInt(KEY_LEVEL, 1).coerceAtLeast(1)
        val bestHeight = prefs.getInt(KEY_BEST_HEIGHT, 0)
        val selectedSkinName = prefs.getString(KEY_SELECTED_SKIN, EggSkin.Classic.name)
        val selectedSkin = EggSkin.entries.firstOrNull { it.name == selectedSkinName } ?: EggSkin.Classic
        val ownedNames = prefs.getStringSet(KEY_OWNED_SKINS, setOf(EggSkin.Classic.name)).orEmpty()
        val ownedSkins = ownedNames.mapNotNull { name -> EggSkin.entries.firstOrNull { it.name == name } }.toSet()
        val ownedWithDefault = ownedSkins.ifEmpty { setOf(EggSkin.Classic) }

        return PlayerProgress(
            coins = coins,
            level = level,
            bestHeight = bestHeight,
            selectedSkin = if (ownedWithDefault.contains(selectedSkin)) selectedSkin else EggSkin.Classic,
            ownedSkins = ownedWithDefault
        )
    }

    private fun persist(progress: PlayerProgress) {
        prefs.edit()
            .putInt(KEY_COINS, progress.coins)
            .putInt(KEY_LEVEL, progress.level)
            .putInt(KEY_BEST_HEIGHT, progress.bestHeight)
            .putString(KEY_SELECTED_SKIN, progress.selectedSkin.name)
            .putStringSet(KEY_OWNED_SKINS, progress.ownedSkins.map { it.name }.toSet())
            .apply()
    }
}
