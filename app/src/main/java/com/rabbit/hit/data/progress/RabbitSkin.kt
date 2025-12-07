package com.rabbit.hit.data.progress

enum class RabbitSkin(
    val title: String,
    val description: String,
    val price: Int,
    val previewRes: Int,
    val gameRes: Int,
) {
    Classic(
        title = "Classic",
        description = "Classic rabbit with carrot",
        price = 0,
        previewRes = com.rabbit.hit.R.drawable.rabbit_1,
        gameRes = com.rabbit.hit.R.drawable.rabbit_1_back,
    ),
    Wizard(
        title = "Wizard",
        description = "Wizard rabbit with hat",
        price = 75,
        previewRes = com.rabbit.hit.R.drawable.rabbit_4,
        gameRes = com.rabbit.hit.R.drawable.rabbit_4_back,
    ),
    Space(
        title = "Space",
        description = "Astronaut rabit with comet carrot",
        price = 125,
        previewRes = com.rabbit.hit.R.drawable.rabbit_2,
        gameRes = com.rabbit.hit.R.drawable.rabbit_2_back,
    ),
    Sport(
        title = "Sport",
        description = "Sport rabit in outfit",
        price = 200,
        previewRes = com.rabbit.hit.R.drawable.rabbit_3,
        gameRes = com.rabbit.hit.R.drawable.rabbit_3_back,
    );
}
