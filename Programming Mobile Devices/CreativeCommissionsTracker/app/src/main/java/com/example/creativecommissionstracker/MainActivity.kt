package com.example.creativecommissionstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.creativecommissionstracker.ui.AppNavHost
import com.example.creativecommissionstracker.ui.theme.ArtAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val appViewModel: AppViewModel = viewModel()

            ArtAppTheme(
                artThemeType = appViewModel.currentTheme
            ) {

                AppNavHost(appViewModel = appViewModel)
            }


        }
    }
}

