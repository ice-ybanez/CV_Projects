package com.example.creativecommissionstracker.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.creativecommissionstracker.data.FirebaseRepository
import com.example.creativecommissionstracker.model.Artist
import com.example.creativecommissionstracker.model.Artwork
import kotlin.collections.filter

data class HomeUiState(
    val isLoading: Boolean = true,
    val artworks: List<Artwork> = emptyList(),
    val artists: List<Artist> = emptyList(),

    // Filters
    val selectedArtType: String? = null,      // "contemporary", "custom", "portrait" or null for all
    val selectedMedium: String? = null,       // "acrylic", "oil" or null
    val selectedStyle: String? = null,        // "abstract", "pop", "photorealistic" or null
    val selectedPriceFilter: String? = null   // "UNDER_200", "UNDER_500", "UNDER_1000" or null
)

class HomeViewModel(
    private val repo: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    // Keep the full list of artworks here, apply filters on top
    private var allArtworks: List<Artwork> = emptyList()

    init {
        loadFeatured()
    }

    fun loadFeatured() {
        uiState = uiState.copy(isLoading = true)

        repo.getFeaturedArtworks { artworks ->
            allArtworks = artworks

            repo.getFeaturedArtists { artists ->
                uiState = uiState.copy(
                    isLoading = false,
                    artists = artists
                )
                applyFilters()
            }
        }
    }

    // FILTERS SETTERS

    fun setArtTypeFilter(type: String?) {
        uiState = uiState.copy(selectedArtType = type)
        applyFilters()
    }

    fun setMediumFilter(medium: String?) {
        uiState = uiState.copy(selectedMedium = medium)
        applyFilters()
    }

    fun setStyleFilter(style: String?) {
        uiState = uiState.copy(selectedStyle = style)
        applyFilters()
    }

    fun setPriceFilter(priceTag: String?) {
        uiState = uiState.copy(selectedPriceFilter = priceTag)
        applyFilters()
    }

    // APPLY FILTERS TO ALL ARTWORKS

    private fun applyFilters() {
        var filtered = allArtworks

        uiState.selectedArtType?.let { type ->
            filtered = filtered.filter {
                it.artType.equals(type, ignoreCase = true)
            }
        }

        uiState.selectedMedium?.let { medium ->
            filtered = filtered.filter {
                it.medium.equals(medium, ignoreCase = true)
            }
        }

        uiState.selectedStyle?.let { style ->
            filtered = filtered.filter {
                it.style.equals(style, ignoreCase = true)
            }
        }

        uiState.selectedPriceFilter?.let { tag ->
            val maxPrice = when (tag) {
                "UNDER_200" -> 200.0
                "UNDER_500" -> 500.0
                "UNDER_1000" -> 1000.0
                else -> null
            }

            if (maxPrice != null) {
                filtered = filtered.filter { it.price <= maxPrice }
            }

        }

        uiState = uiState.copy(artworks = filtered)


    }
}

