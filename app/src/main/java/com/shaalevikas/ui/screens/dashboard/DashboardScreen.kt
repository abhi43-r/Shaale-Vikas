package com.shaalevikas.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shaalevikas.data.model.UserProfile
import com.shaalevikas.data.model.UserRole
import com.shaalevikas.ui.components.EmptyState
import com.shaalevikas.ui.components.GradientHero
import com.shaalevikas.ui.components.LoadingState
import com.shaalevikas.ui.components.NeedCard
import com.shaalevikas.ui.components.SectionHeader
import com.shaalevikas.ui.components.SortChips
import com.shaalevikas.utils.UiState
import com.shaalevikas.viewmodel.NeedsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    profile: UserProfile?,
    onOpenNeed: (String) -> Unit,
    onOpenHallOfFame: () -> Unit,
    onLogout: () -> Unit,
    onEditNeed: (String) -> Unit,
    viewModel: NeedsViewModel = hiltViewModel()
) {
    val needsState by viewModel.needsState.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val refreshing by viewModel.refreshing.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Shaale-Vikas") },
                actions = {
                    IconButton(onClick = onOpenHallOfFame) {
                        Icon(Icons.Outlined.Celebration, contentDescription = "Hall of Fame")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Outlined.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = needsState) {
                is UiState.Loading -> LoadingState()
                is UiState.Error -> EmptyState(title = "Unable to load needs", message = state.message, actionLabel = "Retry", onAction = viewModel::refresh)
                is UiState.Success -> {
                    val items = state.data
                    if (items.isEmpty()) {
                        EmptyState(
                            title = "No needs yet",
                            message = if (profile?.userRole == UserRole.ADMIN) "Add the first school need to start rallying alumni support." else "Check back soon for new infrastructure requests.",
                            actionLabel = if (profile?.userRole == UserRole.ADMIN) "Refresh" else null,
                            onAction = if (profile?.userRole == UserRole.ADMIN) viewModel::refresh else null
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                GradientHero(
                                    title = "Welcome, ${profile?.name?.substringBefore(" ") ?: "supporter"}",
                                    subtitle = if (profile?.userRole == UserRole.ADMIN) {
                                        "Highlight urgent school upgrades, upload progress photos, and keep the alumni network informed in real time."
                                    } else {
                                        "Discover verified needs from your school and pledge support with complete visibility."
                                    }
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = filter.query,
                                    onValueChange = viewModel::updateQuery,
                                    label = { Text("Search needs") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            item {
                                SectionHeader(title = "Sort & explore", actionLabel = "Hall of Fame", onAction = onOpenHallOfFame)
                            }
                            item {
                                SortChips(selected = filter.sortOption, onSelected = viewModel::updateSort)
                            }
                            items(items, key = { it.id }) { need ->
                                NeedCard(
                                    need = need,
                                    onClick = { onOpenNeed(need.id) },
                                    onEditClick = if (profile?.userRole == UserRole.ADMIN) ({ onEditNeed(need.id) }) else null
                                )
                            }
                        }
                    }
                }
                UiState.Idle -> Unit
            }
        }
    }
}
