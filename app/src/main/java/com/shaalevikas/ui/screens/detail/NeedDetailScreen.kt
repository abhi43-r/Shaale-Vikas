package com.shaalevikas.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shaalevikas.ui.components.EmptyState
import com.shaalevikas.ui.components.ImageStrip
import com.shaalevikas.ui.components.LabelValue
import com.shaalevikas.ui.components.LoadingState
import com.shaalevikas.ui.components.PledgeTile
import com.shaalevikas.ui.components.SectionHeader
import com.shaalevikas.utils.UiState
import com.shaalevikas.utils.asCurrency
import com.shaalevikas.viewmodel.NeedDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeedDetailScreen(
    isAdmin: Boolean,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: NeedDetailViewModel = hiltViewModel()
) {
    val needState by viewModel.needState.collectAsStateWithLifecycle()
    val pledgesState by viewModel.pledgesState.collectAsStateWithLifecycle()
    val pledgeActionState by viewModel.pledgeState.collectAsStateWithLifecycle()
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var amount by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(pledgeActionState) {
        when (val state = pledgeActionState) {
            is UiState.Success -> {
                showDialog = false
                amount = ""
                note = ""
                snackbarHostState.showSnackbar("Pledge recorded successfully.")
                viewModel.resetPledgeState()
            }
            is UiState.Error -> snackbarHostState.showSnackbar(state.message)
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Need Details") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                },
                actions = {
                    val currentNeed = (needState as? UiState.Success)?.data
                    if (isAdmin && currentNeed != null) {
                        TextButton(onClick = { onEdit(currentNeed.id) }) {
                            Text("Edit")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = needState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> EmptyState("Need unavailable", state.message)
            is UiState.Success -> {
                val need = state.data ?: return@Scaffold
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    item {
                        Text(need.title, style = MaterialTheme.typography.headlineMedium)
                    }
                    item {
                        Text(need.description, style = MaterialTheme.typography.bodyLarge)
                    }
                    item {
                        LinearProgressIndicator(progress = { need.progress }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        LabelValue(label = "Funding progress", value = "${need.amountCollected.asCurrency()} of ${need.estimatedCost.asCurrency()}")
                    }
                    item {
                        LabelValue(label = "Remaining", value = need.amountRemaining.asCurrency())
                    }
                    item {
                        LabelValue(label = "Priority", value = need.priority)
                    }
                    item {
                        SectionHeader(title = "Impact Photos")
                    }
                    item {
                        ImageStrip(urls = listOfNotNull(need.heroImageUrl, need.beforeImageUrl, need.afterImageUrl))
                    }
                    item {
                        SectionHeader(title = "Recent Pledges")
                    }
                    when (val pledgeState = pledgesState) {
                        is UiState.Success -> {
                            if (pledgeState.data.isEmpty()) {
                                item { Text("No pledges yet. Be the first to support this need.") }
                            } else {
                                items(pledgeState.data, key = { it.id }) { pledge ->
                                    PledgeTile(pledge = pledge)
                                }
                            }
                        }
                        is UiState.Error -> item { Text(pledgeState.message) }
                        else -> item { Text("Loading pledges...") }
                    }
                    item {
                        Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Pledge Support")
                        }
                    }
                }
            }
            UiState.Idle -> Unit
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = { viewModel.submitPledge(amount.toDoubleOrNull() ?: 0.0, note) }) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Simulate a pledge") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") })
                    OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Support note") })
                }
            }
        )
    }
}
