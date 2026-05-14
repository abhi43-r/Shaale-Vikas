package com.shaalevikas.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import coil.compose.AsyncImage
import com.shaalevikas.data.model.NeedItem
import com.shaalevikas.ui.components.LoadingState
import com.shaalevikas.utils.UiState
import com.shaalevikas.viewmodel.AdminNeedEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNeedEditorScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: AdminNeedEditorViewModel = hiltViewModel()
) {
    val editorState by viewModel.editorState.collectAsStateWithLifecycle()
    val existingNeedState by viewModel.existingNeedState.collectAsStateWithLifecycle()
    val existingNeed = (existingNeedState as? UiState.Success)?.data
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("Rural Campus") }
    var category by rememberSaveable { mutableStateOf("Infrastructure") }
    var priority by rememberSaveable { mutableStateOf("High") }
    var estimatedCost by rememberSaveable { mutableStateOf("") }
    var status by rememberSaveable { mutableStateOf("Open") }
    var heroUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var beforeUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var afterUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val heroPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { heroUri = it }
    val beforePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { beforeUri = it }
    val afterPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { afterUri = it }

    LaunchedEffect(existingNeed?.id) {
        if (existingNeed != null && title.isBlank()) {
            title = existingNeed.title
            description = existingNeed.description
            location = existingNeed.location
            category = existingNeed.category
            priority = existingNeed.priority
            estimatedCost = existingNeed.estimatedCost.toString()
            status = existingNeed.status
        }
    }

    LaunchedEffect(editorState) {
        when (val state = editorState) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar("Need saved successfully.")
                viewModel.clearState()
                onSaved()
            }
            is UiState.Error -> snackbarHostState.showSnackbar(state.message)
            else -> Unit
        }
    }

    if (existingNeedState is UiState.Loading) {
        LoadingState()
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingNeed == null) "Add School Need" else "Edit School Need") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Text("Capture a sharp, transparent update that alumni can trust.", style = MaterialTheme.typography.bodyLarge) }
            item { OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Need title") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("School location") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(value = priority, onValueChange = { priority = it }, label = { Text("Priority") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Status") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(value = estimatedCost, onValueChange = { estimatedCost = it }, label = { Text("Estimated cost") }, modifier = Modifier.fillMaxWidth()) }
            item {
                Button(onClick = { heroPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Pick hero image")
                }
            }
            item {
                AsyncImage(model = heroUri ?: existingNeed?.heroImageUrl, contentDescription = null, modifier = Modifier.fillMaxWidth())
            }
            item {
                Button(onClick = { beforePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Pick before image")
                }
            }
            item {
                AsyncImage(model = beforeUri ?: existingNeed?.beforeImageUrl, contentDescription = null, modifier = Modifier.fillMaxWidth())
            }
            item {
                Button(onClick = { afterPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Pick after image")
                }
            }
            item {
                AsyncImage(model = afterUri ?: existingNeed?.afterImageUrl, contentDescription = null, modifier = Modifier.fillMaxWidth())
            }
            item {
                Button(
                    onClick = {
                        val payload = NeedItem(
                            id = existingNeed?.id.orEmpty(),
                            title = title,
                            description = description,
                            location = location,
                            category = category,
                            priority = priority,
                            estimatedCost = estimatedCost.toDoubleOrNull() ?: 0.0,
                            amountCollected = existingNeed?.amountCollected ?: 0.0,
                            heroImageUrl = existingNeed?.heroImageUrl,
                            beforeImageUrl = existingNeed?.beforeImageUrl,
                            afterImageUrl = existingNeed?.afterImageUrl,
                            status = status,
                            createdBy = existingNeed?.createdBy.orEmpty(),
                            createdAt = existingNeed?.createdAt ?: System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        if (existingNeed == null) {
                            viewModel.createNeed(payload, heroUri)
                        } else {
                            viewModel.updateNeed(payload, heroUri, beforeUri, afterUri)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (existingNeed == null) "Create Need" else "Save Changes")
                }
            }
            if (existingNeed != null) {
                item {
                    TextButton(
                        onClick = { viewModel.deleteNeed(existingNeed.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delete Need")
                    }
                }
            }
        }
    }
}
