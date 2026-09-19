package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.navigation.OmniAppNav
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.OmniViewModel
import com.example.ui.viewmodel.OmniViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: OmniViewModel by viewModels {
        OmniViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userPrefs by viewModel.userPreferences.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val isDark = when (userPrefs.themeMode) {
                "dark" -> true
                "light" -> false
                else -> systemDark
            }

            MyApplicationTheme(darkTheme = isDark) {
                OmniAppNav(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

