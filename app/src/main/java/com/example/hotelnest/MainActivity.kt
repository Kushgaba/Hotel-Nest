package com.example.hotelnest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.hotelnest.ui.BookNestApp
import com.example.booknest.ui.theme.HotelNestTheme
import com.example.hotelnest.ui.HotelCard
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen: SplashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HotelNestTheme {
                val systemUiController = rememberSystemUiController()
                val statusBarColor = Color(0xFF00668B)

                systemUiController.setStatusBarColor(
                    color = statusBarColor,
                    darkIcons = false
                )
                systemUiController.setNavigationBarColor(
                    color = Color.Black
                )
                BookNestApp()
            }
        }
    }
}

