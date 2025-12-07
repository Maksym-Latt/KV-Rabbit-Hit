package com.rabbit.hit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.rabbit.hit.audio.SoundscapeManager
import com.rabbit.hit.ui.main.root.AppRoot
import com.rabbit.hit.ui.theme.RabbitHitTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var audio: SoundscapeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideBars()

        setContent {
            RabbitHitTheme {
                AppRoot()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideBars()
    }

    override fun onResume() {
        super.onResume()
        audio.unfreezeTheme()
    }

    override fun onPause() {
        audio.freezeTheme()
        super.onPause()
    }

    private fun hideBars() {
        val ctlr = WindowInsetsControllerCompat(window, window.decorView)
        ctlr.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        ctlr.hide(WindowInsetsCompat.Type.systemBars())
    }
}