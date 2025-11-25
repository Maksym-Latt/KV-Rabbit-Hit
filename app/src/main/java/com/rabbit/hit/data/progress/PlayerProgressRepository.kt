package com.rabbit.hit.data.progress

import kotlinx.coroutines.flow.StateFlow

interface PlayerProgressRepository {
    val progress: StateFlow<PlayerProgress>

    fun recordFinishedRun(score: Int, coinsEarned: Int)
    fun selectSkin(skin: RabbitSkin)
    fun buySkin(skin: RabbitSkin): Boolean
}
