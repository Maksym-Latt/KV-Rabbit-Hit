package com.rabbit.hit.ui.main.root

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rabbit.hit.audio.rememberAudioController
import com.rabbit.hit.ui.main.gamescreen.GameScreen
import com.rabbit.hit.ui.main.gamescreen.GameResult
import com.rabbit.hit.ui.main.menuscreen.MainViewModel
import com.rabbit.hit.ui.main.menuscreen.MenuScreen
import com.rabbit.hit.ui.main.menuscreen.overlay.SettingsOverlay
import com.rabbit.hit.ui.main.menuscreen.overlay.ShopOverlay

@Composable
fun AppRoot(
    vm: MainViewModel = hiltViewModel(),
) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    var showMenuSettings by rememberSaveable { mutableStateOf(false) }
    var showMenuPrivacy by rememberSaveable { mutableStateOf(false) }
    var showShop by rememberSaveable { mutableStateOf(false) }
    val audio = rememberAudioController()

    LaunchedEffect(ui.screen) {
        if (ui.screen != MainViewModel.Screen.Menu) {
            showMenuSettings = false
            showMenuPrivacy = false
            showShop = false
        }
        when (ui.screen) {
            MainViewModel.Screen.Menu -> audio.launchMenuTheme()
            MainViewModel.Screen.Game -> audio.launchSessionTheme()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFF3FF),
                        Color(0xFFFFE0C9),
                        Color(0xFFFFE9D0)
                    )
                )
            )
    ) {
        Crossfade(targetState = ui.screen, label = "root_screen") { screen ->
            when (screen) {
                MainViewModel.Screen.Menu ->
                    Box(Modifier.fillMaxSize()) {
                        MenuScreen(
                            state = ui,
                            onStartGame = {
                                showMenuSettings = false
                                showMenuPrivacy = false
                                showShop = false
                                vm.startGame()
                            },
                            onOpenSettings = { showMenuSettings = true },
                            onOpenShop = { showShop = true },
                        )

                        if (showMenuSettings) {
                            SettingsOverlay(
                                onClose = { showMenuSettings = false },
                            )
                        }

                        if (showShop) {
                            ShopOverlay(
                                skins = vm.skins,
                                owned = ui.ownedSkins,
                                selected = ui.selectedSkin,
                                coins = ui.coins,
                                showInsufficientCoinsDialog = ui.showInsufficientCoinsDialog,
                                onDismissInsufficientCoinsDialog = vm::dismissInsufficientCoinsDialog,
                                onClose = {
                                    showShop = false
                                    vm.dismissInsufficientCoinsDialog()
                                },
                                onSelect = vm::selectSkin,
                                onBuy = vm::buySkin
                            )
                        }
                    }

                MainViewModel.Screen.Game ->
                    GameScreen(
                        skin = ui.selectedSkin,
                        onExitToMenu = { result: GameResult ->
                            showMenuSettings = false
                            showMenuPrivacy = false
                            showShop = false
                            vm.backToMenu(result)
                        }
                    )
            }
        }
    }
}
