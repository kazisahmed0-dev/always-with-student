package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.QuickCaptureType
import com.example.ui.ScreenTab
import com.example.ui.components.QuickCaptureBottomSheet
import com.example.ui.screens.CalculatorsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotesAndLibraryScreen
import com.example.ui.screens.PlannerAndFocusScreen
import com.example.ui.screens.ProfileAndLifeScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AlwaysWithStudentApp(viewModel = mainViewModel)
            }
        }
    }
}

private data class NavigationItemData(
    val tab: ScreenTab,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun AlwaysWithStudentApp(viewModel: MainViewModel) {
    val currentTab by viewModel.currentScreen.collectAsStateWithLifecycle()
    val quickCaptureType by viewModel.quickCaptureType.collectAsStateWithLifecycle()

    val navItems = listOf(
        NavigationItemData(
            tab = ScreenTab.HOME,
            label = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            testTag = "nav_item_home"
        ),
        NavigationItemData(
            tab = ScreenTab.NOTES_LIBRARY,
            label = "Notes & Docs",
            selectedIcon = Icons.Filled.MenuBook,
            unselectedIcon = Icons.Outlined.MenuBook,
            testTag = "nav_item_notes"
        ),
        NavigationItemData(
            tab = ScreenTab.CALCULATORS,
            label = "Calculators",
            selectedIcon = Icons.Filled.Calculate,
            unselectedIcon = Icons.Outlined.Calculate,
            testTag = "nav_item_calculators"
        ),
        NavigationItemData(
            tab = ScreenTab.PLANNER_FOCUS,
            label = "Planner",
            selectedIcon = Icons.Filled.Checklist,
            unselectedIcon = Icons.Outlined.Checklist,
            testTag = "nav_item_planner"
        ),
        NavigationItemData(
            tab = ScreenTab.PROFILE_LIFE,
            label = "Life",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            testTag = "nav_item_profile"
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.navigationBars,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_bottom_nav_bar")
            ) {
                navItems.forEach { item ->
                    val isSelected = currentTab == item.tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setScreen(item.tab) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openQuickCapture() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .testTag("fab_quick_capture")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Quick Capture",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label = "screen_transition"
        ) { tab ->
            when (tab) {
                ScreenTab.HOME -> HomeScreen(viewModel = viewModel)
                ScreenTab.NOTES_LIBRARY -> NotesAndLibraryScreen(viewModel = viewModel)
                ScreenTab.CALCULATORS -> CalculatorsScreen(viewModel = viewModel)
                ScreenTab.PLANNER_FOCUS -> PlannerAndFocusScreen(viewModel = viewModel)
                ScreenTab.PROFILE_LIFE -> ProfileAndLifeScreen(viewModel = viewModel)
            }
        }
    }

    // Quick Capture Bottom Sheet Modal
    if (quickCaptureType != QuickCaptureType.NONE) {
        QuickCaptureBottomSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.closeQuickCapture() }
        )
    }
}

