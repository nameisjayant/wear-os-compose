package com.example.focusworkwearapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.focusworkwearapp.presentation.features.navigation.MainNavigation
import com.example.focusworkwearapp.presentation.features.ui.screens.TaskScreen
import com.example.focusworkwearapp.presentation.theme.FocusWorkWearAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocusWorkWearAppTheme {
                MainNavigation()
            }
        }
    }
}

