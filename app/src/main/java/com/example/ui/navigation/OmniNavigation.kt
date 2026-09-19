package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.util.AppStrings
import com.example.ui.components.RewardedAdDialog
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CompareScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PremiumScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.screens.VoiceChatDialog
import com.example.ui.viewmodel.OmniViewModel
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val stringKey: String, val icon: ImageVector) {
    object Home : Screen("home", "home", Icons.Default.Home)
    object Chat : Screen("chat", "chat", Icons.Default.ChatBubble)
    object Compare : Screen("compare", "compare", Icons.Default.CompareArrows)
    object Tools : Screen("tools", "tools", Icons.Default.AutoAwesome)
    object Premium : Screen("premium", "premium", Icons.Default.WorkspacePremium)
    object Profile : Screen("profile", "profile", Icons.Default.Person)
}

val bottomNavScreens = listOf(
    Screen.Home,
    Screen.Chat,
    Screen.Compare,
    Screen.Tools,
    Screen.Premium,
    Screen.Profile
)

@Composable
fun OmniAppNav(
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val userPrefs by viewModel.userPreferences.collectAsState()
    val lang = userPrefs.language

    val rewardedAdState by viewModel.adsManager.rewardedAdState.collectAsState()
    val voiceChatActive by viewModel.voiceChatActive.collectAsState()
    val userNotice by viewModel.userNotice.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userNotice) {
        val notice = userNotice
        if (notice != null) {
            snackbarHostState.showSnackbar(notice)
            viewModel.dismissNotice()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                bottomNavScreens.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    val isPremiumTab = screen == Screen.Premium

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.route,
                                modifier = Modifier.size(21.dp)
                            )
                        },
                        label = {
                            Text(
                                text = AppStrings.get(screen.stringKey, lang),
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = if (isPremiumTab) Color(0xFFF59E0B) else MaterialTheme.colorScheme.primary,
                            selectedTextColor = if (isPremiumTab) Color(0xFFF59E0B) else MaterialTheme.colorScheme.primary,
                            indicatorColor = (if (isPremiumTab) Color(0xFFF59E0B) else MaterialTheme.colorScheme.primary).copy(alpha = 0.15f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_item_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToChat = { model ->
                        viewModel.selectModel(model)
                        navController.navigate(Screen.Chat.route)
                    },
                    onNavigateToCompare = {
                        navController.navigate(Screen.Compare.route)
                    },
                    onNavigateToTool = { tool ->
                        viewModel.openTool(tool)
                        navController.navigate(Screen.Tools.route)
                    },
                    onNavigateToPremium = {
                        navController.navigate(Screen.Premium.route)
                    }
                )
            }

            composable(Screen.Chat.route) {
                ChatScreen(
                    viewModel = viewModel,
                    onNavigateToPremium = {
                        navController.navigate(Screen.Premium.route)
                    }
                )
            }

            composable(Screen.Compare.route) {
                CompareScreen(
                    viewModel = viewModel,
                    onNavigateToPremium = {
                        navController.navigate(Screen.Premium.route)
                    }
                )
            }

            composable(Screen.Tools.route) {
                ToolsScreen(
                    viewModel = viewModel,
                    onNavigateToPremium = {
                        navController.navigate(Screen.Premium.route)
                    }
                )
            }

            composable(Screen.Premium.route) {
                PremiumScreen(
                    viewModel = viewModel
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToPremium = {
                        navController.navigate(Screen.Premium.route)
                    }
                )
            }
        }
    }

    // Rewarded Ad Dialog Overlay
    if (rewardedAdState != null) {
        val state = rewardedAdState!!
        RewardedAdDialog(
            state = state,
            onRunTimer = {
                scope.launch {
                    viewModel.adsManager.runRewardedAdTimer { reward ->
                        scope.launch {
                            viewModel.prefsManager.addBonusMessages(reward)
                        }
                    }
                }
            },
            onDismiss = {
                viewModel.adsManager.dismissRewardedAd()
            }
        )
    }

    // Voice Chat Fullscreen Dialog Overlay
    if (voiceChatActive) {
        VoiceChatDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closeVoiceChat() }
        )
    }
}
