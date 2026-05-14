package com.shaalevikas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shaalevikas.data.model.NeedItem
import com.shaalevikas.data.model.NeedSortOption
import com.shaalevikas.data.model.Pledge
import com.shaalevikas.ui.theme.Amber400
import com.shaalevikas.ui.theme.Forest700
import com.shaalevikas.ui.theme.Sky100
import com.shaalevikas.ui.theme.Slate400
import com.shaalevikas.ui.theme.Slate700
import com.shaalevikas.utils.asCurrency

@Composable
fun GradientHero(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Forest700, Amber400)
                    )
                )
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(title, style = MaterialTheme.typography.headlineMedium, color = androidx.compose.ui.graphics.Color.White)
                Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f))
            }
        }
    }
}

@Composable
fun NeedCard(
    need: NeedItem,
    onClick: () -> Unit,
    onEditClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(
                model = need.heroImageUrl,
                contentDescription = need.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Sky100),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(need.title, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(need.description, style = MaterialTheme.typography.bodyMedium, color = Slate700, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                AssistChip(
                    onClick = {},
                    label = { Text(need.priority) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = Amber400.copy(alpha = 0.2f))
                )
            }
            LinearProgressIndicator(progress = { need.progress }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Raised", style = MaterialTheme.typography.labelLarge, color = Slate400)
                    Text(need.amountCollected.asCurrency(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Goal", style = MaterialTheme.typography.labelLarge, color = Slate400)
                    Text(need.estimatedCost.asCurrency(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
            if (onEditClick != null) {
                OutlinedButton(onClick = onEditClick, modifier = Modifier.align(Alignment.End)) {
                    Text("Manage Need")
                }
            }
        }
    }
}

@Composable
fun PledgeTile(pledge: Pledge) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(pledge.donorName, style = MaterialTheme.typography.titleMedium)
            Text(pledge.note.ifBlank { "Supporting this school need." }, style = MaterialTheme.typography.bodyMedium, color = Slate700)
        }
        Text(pledge.amount.asCurrency(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Forest700)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortChips(
    selected: NeedSortOption,
    onSelected: (NeedSortOption) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(NeedSortOption.entries) { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelected(option) },
                label = { Text(option.name.replace("_", " ")) }
            )
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Sky100),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Groups, contentDescription = null, tint = Forest700)
            }
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(message, style = MaterialTheme.typography.bodyMedium, color = Slate700)
            if (actionLabel != null && onAction != null) {
                Button(onClick = onAction) {
                    Text(actionLabel)
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun LabelValue(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = Slate400)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun ImageStrip(urls: List<String>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(urls) { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp, 120.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Sky100),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (actionLabel != null && onAction != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = Forest700,
                modifier = Modifier.clickable(onClick = onAction)
            )
        }
    }
}

@Composable
fun Spacer16() {
    Spacer(modifier = Modifier.height(16.dp))
}
