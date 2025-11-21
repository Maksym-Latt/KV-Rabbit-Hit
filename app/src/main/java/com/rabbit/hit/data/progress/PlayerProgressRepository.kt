package com.rabbit.hit.data.progress

import kotlinx.coroutines.flow.StateFlow

interface PlayerProgressRepository {
    val progress: StateFlow<PlayerProgress>

    fun recordFinishedRun(coinsEarned: Int, height: Int, level: Int)
    fun saveLevel(level: Int)
    fun selectSkin(skin: EggSkin)
    fun buySkin(skin: EggSkin): Boolean
}
