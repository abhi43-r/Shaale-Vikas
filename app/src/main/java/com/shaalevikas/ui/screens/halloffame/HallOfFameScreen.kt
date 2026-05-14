package com.shaalevikas.ui.screens.halloffame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shaalevikas.ui.components.EmptyState
import com.shaalevikas.ui.components.LoadingState
import com.shaalevikas.utils.UiState
import com.shaalevikas.utils.asCurrency
import com.shaalevikas.viewmodel.HallOfFameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HallOfFameScreen(
    onBack: () -> Unit,
    viewModel: HallOfFameViewModel = hiltViewModel()
) {
    val hallState by viewModel.hallOfFameState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Donor Hall of Fame") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { innerPadding ->
        when (val state = hallState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> EmptyState("Unable to load contributors", state.message)
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyState("No contributors yet", "Pledges will appear here in a live leaderboard.")
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        itemsIndexed(state.data, key = { _, item -> item.userId }) { index, entry ->
                            Text(
                                "${index + 1}. ${entry.donorName} • ${entry.totalContribution.asCurrency()} • ${entry.pledgeCount} pledges",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
            UiState.Idle -> Unit
        }
    }
}
