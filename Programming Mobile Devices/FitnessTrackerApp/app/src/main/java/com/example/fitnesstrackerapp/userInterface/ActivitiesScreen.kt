package com.example.fitnesstrackerapp.userInterface

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fitnesstrackerapp.viewModel.FitnessViewModel

// Screen 1: Item List (LazyColumn)
@Composable
fun ActivitiesScreen(
    onOpenDetails: (String) -> Unit,
    vm: FitnessViewModel
) {
    val activities = vm.activities

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(activities, key = { it.id })
        { activity ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenDetails(activity.id) }   // navigate to details
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (activity.imageRes != null) {
                    Image(
                        painter = painterResource(id = activity.imageRes),
                        contentDescription = activity.name,
                        modifier = Modifier
                            .size(56.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(12.dp))
                }

                Column{
                    Text(
                        activity.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        activity.description,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            // separate between rows
            Divider()
        }
    }
}
