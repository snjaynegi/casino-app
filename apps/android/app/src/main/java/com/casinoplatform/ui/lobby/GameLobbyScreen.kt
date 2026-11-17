package com.casinoplatform.ui.lobby

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casinoplatform.data.model.GameItem
import com.casinoplatform.data.repository.GamesRepository
import com.casinoplatform.ui.components.EmptyStateView
import com.casinoplatform.ui.components.GameCard
import com.casinoplatform.ui.theme.DarkBackground
import com.casinoplatform.ui.theme.DarkSurface
import com.casinoplatform.ui.theme.DarkSurfaceBorder
import com.casinoplatform.ui.theme.DarkSurfaceElevated
import com.casinoplatform.ui.theme.PrimaryGold
import com.casinoplatform.ui.theme.TextPrimary
import com.casinoplatform.ui.theme.TextSecondary

@Composable
fun GameLobbyScreen(
    repository: GamesRepository = remember { GamesRepository() },
    onSelectGame: (GameItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("ALL", "CRASH", "ARCADE", "INSTANT", "FAVORITES")
    val filteredGames = repository.getFilteredGames(selectedCategory, searchQuery)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search games (e.g., Rocket, Mines, Dice)...", color = TextSecondary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGold,
                unfocusedBorderColor = DarkSurfaceBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface
            ),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Category Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 6.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) PrimaryGold else DarkSurfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) PrimaryGold else DarkSurfaceBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) DarkBackground else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Games Grid / List
        if (filteredGames.isEmpty()) {
            EmptyStateView(
                title = "No Games Found",
                description = "Try adjusting your search query or switching categories.",
                actionText = "Show All Games",
                onActionClick = {
                    searchQuery = ""
                    selectedCategory = "ALL"
                }
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredGames, key = { it.id }) { game ->
                    GameCard(
                        title = game.title,
                        category = game.category,
                        rtp = game.rtp,
                        activePlayers = game.activePlayers,
                        onClick = { onSelectGame(game) }
                    )
                }
            }
        }
    }
}
