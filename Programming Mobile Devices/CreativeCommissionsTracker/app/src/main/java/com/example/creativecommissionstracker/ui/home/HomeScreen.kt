package com.example.creativecommissionstracker.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.creativecommissionstracker.AppViewModel
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import com.example.creativecommissionstracker.ui.theme.ArtThemeType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    appViewModel: AppViewModel,
    navController: NavHostController,   // needs to be here for AppNavHost.kt so ignore warning
    vm: HomeViewModel = viewModel()
) {
    val state = vm.uiState
    val context = LocalContext.current

    val currentTheme = appViewModel.currentTheme

    val homeTitleTextStyle = when (currentTheme) {

        ArtThemeType.CONTEMPORARY -> {
            // default style: serif + large
            MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Serif,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
        }

        ArtThemeType.CUSTOM -> {
            // comic-ish: monospace + bold
            MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
        }
        ArtThemeType.PORTRAIT -> {
            // classic/cursive: cursive + italic
            MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Cursive,
                fontStyle = FontStyle.Italic,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
        }
    }

    // for screen enter animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    // for expanding/collapsing filter groups
    var showArtTypeFilter by remember { mutableStateOf(false) }
    var showMediumFilter by remember { mutableStateOf(false) }
    var showStyleFilter by remember { mutableStateOf(false) }
    var showPriceFilter by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Home",
                        style = homeTitleTextStyle
                    )
                }
            )
        }
    ) { padding ->
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                initialOffsetY = { it / 10 }
            ),
            exit = fadeOut(animationSpec = tween(200)) + slideOutVertically(
                targetOffsetY = { it / 10 }
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp) // extra space above bottom bar
                    .verticalScroll(rememberScrollState())
            ) {

                // FILTER HEADER ROW
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterGroupButton(
                        text = "Art type",
                        expanded = showArtTypeFilter
                    ) {
                        showArtTypeFilter = !showArtTypeFilter
                    }
                    FilterGroupButton(
                        text = "Medium",
                        expanded = showMediumFilter
                    ) {
                        showMediumFilter = !showMediumFilter
                    }
                    FilterGroupButton(
                        text = "Style",
                        expanded = showStyleFilter
                    ) {
                        showStyleFilter = !showStyleFilter
                    }
                    FilterGroupButton(
                        text = "Price",
                        expanded = showPriceFilter
                    ) {
                        showPriceFilter = !showPriceFilter
                    }
                }

                Spacer(Modifier.height(8.dp))

                // expanded filter sections (only show when toggled)
                if (showArtTypeFilter) {
                    Spacer(Modifier.height(4.dp))
                    FilterRow(
                        label = "Art Type",
                        options = listOf(
                            null to "All",
                            "contemporary" to "Contemporary",
                            "custom" to "Custom Art",
                            "portrait" to "Portraits"
                        ),
                        selected = state.selectedArtType,
                        onSelectedChange = { type ->
                            vm.setArtTypeFilter(type)
                            appViewModel.setThemeFromArtType(type)
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                }

                if (showMediumFilter) {
                    FilterRow(
                        label = "Medium",
                        options = listOf(
                            null to "All",
                            "acrylic" to "Acrylic",
                            "oil" to "Oil"
                        ),
                        selected = state.selectedMedium,
                        onSelectedChange = { vm.setMediumFilter(it) }
                    )
                    Spacer(Modifier.height(8.dp))
                }

                if (showStyleFilter) {
                    FilterRow(
                        label = "Style",
                        options = listOf(
                            null to "All",
                            "abstract" to "Abstract",
                            "pop" to "Pop",
                            "photorealistic" to "Photorealistic"
                        ),
                        selected = state.selectedStyle,
                        onSelectedChange = { vm.setStyleFilter(it) }
                    )
                    Spacer(Modifier.height(8.dp))
                }

                if (showPriceFilter) {
                    FilterRow(
                        label = "Price",
                        options = listOf(
                            null to "All",
                            "UNDER_200" to "Under €200",
                            "UNDER_500" to "Under €500",
                            "UNDER_1000" to "Under €1000"
                        ),
                        selected = state.selectedPriceFilter,
                        onSelectedChange = { vm.setPriceFilter(it) }
                    )

                    Spacer(Modifier.height(8.dp))

                }

                Spacer(Modifier.height(16.dp))

                // FEATURED ART SECTION
                Text(
                    text = "Featured Art",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(8.dp))

                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        CircularProgressIndicator()

                    }
                } else {
                    if (state.artworks.isEmpty()) {
                        Text("No artworks found with current filters.")
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.artworks) { art ->
                                Card(
                                    modifier = Modifier
                                        .width(350.dp)
                                        .height(380.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column {
                                        AsyncImage(
                                            model = art.imageUrl,
                                            contentDescription = art.title,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(300.dp),
                                            contentScale = ContentScale.Crop
                                        )

                                        Column(
                                            modifier = Modifier.padding(8.dp)
                                        ) {
                                            Text(
                                                text = art.title,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyLarge
                                            )

                                            Spacer(Modifier.height(4.dp))

                                            Text(
                                                text = "€${art.price.toInt()}",
                                                style = MaterialTheme.typography.bodyMedium
                                            )

                                            Spacer(Modifier.height(4.dp))

                                            Text(
                                                text = "${art.medium} - ${art.style}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // FEATURED ARTISTS SECTION
                Text(
                    text = "Featured Artists",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(8.dp))

                if (state.artists.isEmpty() && !state.isLoading) {
                    Text("No artists found.")
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.artists) { artist ->
                            AssistChip(
                                onClick = {
                                    val url = artist.instagramUrl
                                    if (url.isNotBlank()) {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(url)
                                        )
                                        context.startActivity(intent)
                                    }
                                },
                                label = { Text(artist.name) }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FilterRow(
    label: String,
    options: List<Pair<String?, String>>,
    selected: String?,
    onSelectedChange: (String?) -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { (value, text) ->
                val isSelected = selected == value
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        // if user taps the already selected option, clear the filter
                        if (isSelected) {
                            onSelectedChange(null)
                        } else {
                            onSelectedChange(value)
                        }
                    },

                    label = { Text(text) }

                )
            }
        }
    }
}

@Composable
private fun FilterGroupButton(
    text: String,
    expanded: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = if (expanded) "$text ▲" else "$text ▼"
            )
        }
    )
}



