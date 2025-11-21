package com.rabbit.hit.data.progress

data class PlayerProgress(
    val coins: Int = 0,
    val bestScore: Int = 0,
    val selectedSkin: RabbitSkin = RabbitSkin.Classic,
    val ownedSkins: Set<RabbitSkin> = setOf(RabbitSkin.Classic)
)
