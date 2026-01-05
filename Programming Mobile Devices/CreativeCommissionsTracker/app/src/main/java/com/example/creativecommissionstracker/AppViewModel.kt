package com.example.creativecommissionstracker

import androidx.lifecycle.ViewModel
import com.example.creativecommissionstracker.ui.theme.ArtThemeType
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class AppViewModel : ViewModel() {

    var currentTheme: ArtThemeType by mutableStateOf(ArtThemeType.CONTEMPORARY)
        private set

    fun setThemeFromArtType(artType: String?) {
        currentTheme = when (artType?.lowercase()) {
            "contemporary" -> ArtThemeType.CONTEMPORARY
            "custom"       -> ArtThemeType.CUSTOM
            "portrait"     -> ArtThemeType.PORTRAIT
            else           -> ArtThemeType.CONTEMPORARY
        }
    }
}