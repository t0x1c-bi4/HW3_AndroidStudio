package com.example.hw3_androidstudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.hw3_androidstudio.navigation.AppNavigation
import com.example.hw3_androidstudio.ui.theme.HW3_AndroidStudioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HW3_AndroidStudioTheme() {
                AppNavigation()
            }
        }
    }
}