package com.example.mtgachievementleaderboardtracking

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mtgachievementleaderboardtracking.ui.theme.MTGAchievementLeaderboardTrackingTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }

    override fun onResume() {
        super.onResume()

        // Safely handle the status bar visibility based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowInsetsController = window.insetsController
            windowInsetsController?.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        }
    }
}

@Composable
fun PlayerAchievementScreen() {
    val context = LocalContext.current
    val players = remember { loadPlayers(context) }
    val achievements = remember { loadAchievements(context) }

    val selectedPlayers = remember { mutableStateOf(setOf<Int>()) }
    val selectedAchievements = remember { mutableStateOf(setOf<Int>()) }

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .padding(top = 56.dp), // Padding to move below top bar
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
    ) {
        // Players section header
        item {
            Text(
                text = "Players",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
        }

        // Render the list of players
        items(players) { player ->
            PlayerItem(player = player, selectedPlayers = selectedPlayers)
        }

        // Achievements section header
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Achievements",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
        }

        // Render the list of achievements
        items(achievements) { achievement ->
            AchievementItem(achievement = achievement, selectedAchievements = selectedAchievements)
        }

        // Submit button at the bottom
        item {
            Button(
                onClick = {
                    selectedPlayers.value = emptySet()
                    selectedAchievements.value = emptySet()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }
        }
    }
}

@Composable
fun CustomScrollbar(scrollState: ScrollState, modifier: Modifier = Modifier) {
    // Calculate the total height and current scroll fraction
    val totalHeight = scrollState.maxValue.toFloat() // Total scrollable height
    val scrollFraction = if (totalHeight > 0) {
        scrollState.value / totalHeight
    } else {
        0f
    }

    // Fraction of the visible area in the total content (for example, 20% visible area = 0.2f)
    val scrollbarHeightFraction = 0.2f // Adjust this to control how much space the scrollbar takes
    val scrollbarOffsetFraction = scrollFraction * (1f - scrollbarHeightFraction)

    // State to control scrollbar visibility based on scroll activity
    val isScrolling = remember { mutableStateOf(false) }

    // Update isScrolling based on the scroll state (whether it's being actively scrolled)
    LaunchedEffect(scrollState.value) {
        isScrolling.value = scrollState.isScrollInProgress
    }

    Box(
        modifier = modifier
            .fillMaxHeight() // Ensure the scrollbar stretches vertically
            .width(8.dp)     // Set width of the scrollbar
            .padding(end = 4.dp) // Add padding to right for some spacing
    ) {
        // Only show the scrollbar when scrolling is in progress
        if (isScrolling.value) {
            // Scrollbar itself
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd) // Place scrollbar at the top-end (right-hand side)
                    .fillMaxHeight(fraction = scrollbarHeightFraction) // Set scrollbar height fraction
                    .offset(y = scrollbarOffsetFraction.dp) // Offset scrollbar position based on scroll position
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), RoundedCornerShape(50))
                    .width(4.dp) // Set scrollbar width
            )
        }
    }
}


@Composable
fun PlayerItem(player: Player, selectedPlayers: MutableState<Set<Int>>) {
    // Handle the checkbox toggling when the row is clicked
    val isChecked = player.id in selectedPlayers.value

    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Toggle the checkbox when clicking anywhere in the row
                        selectedPlayers.value = if (isChecked) {
                            selectedPlayers.value - player.id
                        } else {
                            selectedPlayers.value + player.id
                        }
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = player.name,
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.weight(1f)
                )
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = null // We handle the checkbox change on row click
                )
            }
        }
    }
}

@Composable
fun AchievementItem(achievement: Achievement, selectedAchievements: MutableState<Set<Int>>) {
    // Handle the checkbox toggling when the row is clicked
    val isChecked = achievement.id in selectedAchievements.value

    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Toggle the checkbox when clicking anywhere in the row
                        selectedAchievements.value = if (isChecked) {
                            selectedAchievements.value - achievement.id
                        } else {
                            selectedAchievements.value + achievement.id
                        }
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = achievement.name,
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.weight(1f)
                )
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = null // We handle the checkbox change on row click
                )
            }
        }
    }
}

// Adjusted scaledFontSize function
@Composable
fun scaledFontSize(availableWidth: Dp, text: String): TextUnit {
    val maxFontSize = 24.sp
    val minFontSize = 16.sp

    // Estimate text width based on length and available space
    val estimatedTextWidth = text.length * 10 // Rough estimate: 10px per character
    val scalingFactor = availableWidth.value / estimatedTextWidth

    // Scale font size and constrain to min/max values
    val scaledSize = (scalingFactor * maxFontSize.value).coerceIn(minFontSize.value, maxFontSize.value)
    return scaledSize.sp
}

@Composable
fun CustomTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Navigation Icon (Menu)
        IconButton(onClick = { /* Handle navigation click */ }) {
            Icon(Icons.Filled.Menu, contentDescription = "Menu")
        }

        // Title
        Text(
            text = "Player Achievement Tracker",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f), // Ensures title is centered
            textAlign = TextAlign.Center
        )

        // Optional action button
        IconButton(onClick = { /* Handle action button click */ }) {
            Icon(Icons.Filled.Search, contentDescription = "Search")
        }
    }
}

@Composable
fun MyApp() {
    // Wrap the entire app in the custom theme
    MTGAchievementLeaderboardTrackingTheme {
        // Main app content goes here
        Scaffold(
            topBar = {
                CustomTopBar()  // Only include the top bar here
            },
            content = {
                PlayerAchievementScreen()  // Make sure you only call PlayerAchievementScreen here
            }
        )
    }
}

fun loadPlayers(context: Context): List<Player> {
    val inputStream = context.assets.open("players.json")
    val reader = InputStreamReader(inputStream)
    val playerType = object : TypeToken<List<Player>>() {}.type
    return Gson().fromJson(reader, playerType)
}

fun loadAchievements(context: Context): List<Achievement> {
    val inputStream = context.assets.open("achievements.json")
    val reader = InputStreamReader(inputStream)
    val achievementType = object : TypeToken<List<Achievement>>() {}.type
    return Gson().fromJson(reader, achievementType)
}
