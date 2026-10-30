package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.AppViewModel
import com.example.ui.navigation.NavRoutes
import com.example.ui.screens.AddMedicineScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.ConfirmationSuccessScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EditMedicineScreen
import com.example.ui.screens.EscalationHistoryScreen
import com.example.ui.screens.LoginGuestScreen
import com.example.ui.screens.MedicineDetailsScreen
import com.example.ui.screens.MedicineHistoryScreen
import com.example.ui.screens.PhotoConfirmationScreen
import com.example.ui.screens.PrivacyScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StockManagementScreen
import com.example.ui.screens.TrustedContactsScreen
import com.example.ui.screens.UserProfileScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SmartEscalationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val navTarget = intent?.getStringExtra("EXTRA_NAV_TARGET")
        val reminderExtraId = intent?.getLongExtra("EXTRA_REMINDER_ID", -1L) ?: -1L

        setContent {
            val profile by viewModel.userProfile.collectAsState()

            SmartEscalationTheme(darkTheme = profile.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    LaunchedEffect(navTarget) {
                        if (navTarget != null) {
                            when (navTarget) {
                                "escalations" -> navController.navigate(NavRoutes.ESCALATION_HISTORY)
                                "stock" -> navController.navigate(NavRoutes.STOCK_MANAGEMENT)
                                "reminder_dialog" -> {
                                    if (reminderExtraId > 0) {
                                        navController.navigate(NavRoutes.photoConfirmationRoute(reminderExtraId))
                                    }
                                }
                            }
                        }
                    }

                    MainAppNavigation(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun MainAppNavigation(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Show bottom bar only on primary tabs
    val bottomBarRoutes = listOf(
        NavRoutes.DASHBOARD,
        NavRoutes.TRUSTED_CONTACTS,
        NavRoutes.MEDICINE_HISTORY,
        NavRoutes.ANALYTICS,
        NavRoutes.SETTINGS
    )
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.SPLASH,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.SPLASH) {
                SplashScreen(
                    onTimeout = {
                        navController.navigate(NavRoutes.WELCOME) {
                            popUpTo(NavRoutes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            composable(NavRoutes.WELCOME) {
                WelcomeScreen(
                    onContinue = {
                        navController.navigate(NavRoutes.DASHBOARD) {
                            popUpTo(NavRoutes.WELCOME) { inclusive = true }
                        }
                    },
                    onLoginClick = {
                        navController.navigate(NavRoutes.LOGIN)
                    }
                )
            }

            composable(NavRoutes.LOGIN) {
                LoginGuestScreen(
                    viewModel = viewModel,
                    onSuccess = {
                        navController.navigate(NavRoutes.DASHBOARD) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(NavRoutes.DASHBOARD) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToAddMedicine = { navController.navigate(NavRoutes.ADD_MEDICINE) },
                    onNavigateToMedicineDetails = { id -> navController.navigate(NavRoutes.medicineDetailsRoute(id)) },
                    onNavigateToPhotoConfirmation = { id -> navController.navigate(NavRoutes.photoConfirmationRoute(id)) },
                    onNavigateToTrustedContacts = { navController.navigate(NavRoutes.TRUSTED_CONTACTS) },
                    onNavigateToEscalations = { navController.navigate(NavRoutes.ESCALATION_HISTORY) },
                    onNavigateToHistory = { navController.navigate(NavRoutes.MEDICINE_HISTORY) },
                    onNavigateToStock = { navController.navigate(NavRoutes.STOCK_MANAGEMENT) },
                    onNavigateToProfile = { navController.navigate(NavRoutes.USER_PROFILE) }
                )
            }

            composable(NavRoutes.ADD_MEDICINE) {
                AddMedicineScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(
                route = NavRoutes.EDIT_MEDICINE,
                arguments = listOf(navArgument("medicineId") { type = NavType.LongType })
            ) { backStackEntry ->
                val medId = backStackEntry.arguments?.getLong("medicineId") ?: 0L
                EditMedicineScreen(
                    medicineId = medId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(
                route = NavRoutes.MEDICINE_DETAILS,
                arguments = listOf(navArgument("medicineId") { type = NavType.LongType })
            ) { backStackEntry ->
                val medId = backStackEntry.arguments?.getLong("medicineId") ?: 0L
                MedicineDetailsScreen(
                    medicineId = medId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(NavRoutes.editMedicineRoute(id)) },
                    onRefill = { id -> viewModel.refillMedicineStock(id, 30) }
                )
            }

            composable(
                route = NavRoutes.PHOTO_CONFIRMATION,
                arguments = listOf(navArgument("reminderId") { type = NavType.LongType })
            ) { backStackEntry ->
                val remId = backStackEntry.arguments?.getLong("reminderId") ?: 0L
                PhotoConfirmationScreen(
                    reminderId = remId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onConfirmedSuccess = { id, hasPhoto ->
                        navController.navigate(NavRoutes.confirmationSuccessRoute(id, hasPhoto)) {
                            popUpTo(NavRoutes.DASHBOARD)
                        }
                    }
                )
            }

            composable(
                route = NavRoutes.CONFIRMATION_SUCCESS,
                arguments = listOf(
                    navArgument("reminderId") { type = NavType.LongType },
                    navArgument("hasPhoto") { type = NavType.BoolType }
                )
            ) { backStackEntry ->
                val remId = backStackEntry.arguments?.getLong("reminderId") ?: 0L
                val hasPhoto = backStackEntry.arguments?.getBoolean("hasPhoto") ?: false
                ConfirmationSuccessScreen(
                    reminderId = remId,
                    hasPhoto = hasPhoto,
                    viewModel = viewModel,
                    onReturnToDashboard = {
                        navController.navigate(NavRoutes.DASHBOARD) {
                            popUpTo(NavRoutes.DASHBOARD) { inclusive = true }
                        }
                    }
                )
            }

            composable(NavRoutes.TRUSTED_CONTACTS) {
                TrustedContactsScreen(
                    viewModel = viewModel,
                    onBack = { navController.navigate(NavRoutes.DASHBOARD) },
                    onNavigateToEscalationHistory = { navController.navigate(NavRoutes.ESCALATION_HISTORY) }
                )
            }

            composable(NavRoutes.ESCALATION_HISTORY) {
                EscalationHistoryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.MEDICINE_HISTORY) {
                MedicineHistoryScreen(
                    viewModel = viewModel,
                    onBack = { navController.navigate(NavRoutes.DASHBOARD) }
                )
            }

            composable(NavRoutes.ANALYTICS) {
                AnalyticsScreen(
                    viewModel = viewModel,
                    onBack = { navController.navigate(NavRoutes.DASHBOARD) }
                )
            }

            composable(NavRoutes.STOCK_MANAGEMENT) {
                StockManagementScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.SETTINGS) {
                SettingsScreen(
                    viewModel = viewModel,
                    onBack = { navController.navigate(NavRoutes.DASHBOARD) },
                    onNavigateToPrivacy = { navController.navigate(NavRoutes.PRIVACY_DATA) },
                    onNavigateToProfile = { navController.navigate(NavRoutes.USER_PROFILE) }
                )
            }

            composable(NavRoutes.USER_PROFILE) {
                UserProfileScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.PRIVACY_DATA) {
                PrivacyScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("app_bottom_bar")
    ) {
        NavigationBarItem(
            selected = currentRoute == NavRoutes.DASHBOARD,
            onClick = { onNavigate(NavRoutes.DASHBOARD) },
            icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Today") },
            label = { Text("Today") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavyPrimary,
                selectedTextColor = NavyPrimary
            )
        )

        NavigationBarItem(
            selected = currentRoute == NavRoutes.TRUSTED_CONTACTS,
            onClick = { onNavigate(NavRoutes.TRUSTED_CONTACTS) },
            icon = { Icon(Icons.Default.People, contentDescription = "Contacts") },
            label = { Text("Caregivers") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavyPrimary,
                selectedTextColor = NavyPrimary
            )
        )

        NavigationBarItem(
            selected = currentRoute == NavRoutes.MEDICINE_HISTORY,
            onClick = { onNavigate(NavRoutes.MEDICINE_HISTORY) },
            icon = { Icon(Icons.Default.History, contentDescription = "History") },
            label = { Text("History") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavyPrimary,
                selectedTextColor = NavyPrimary
            )
        )

        NavigationBarItem(
            selected = currentRoute == NavRoutes.ANALYTICS,
            onClick = { onNavigate(NavRoutes.ANALYTICS) },
            icon = { Icon(Icons.Default.Analytics, contentDescription = "Analytics") },
            label = { Text("Analytics") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavyPrimary,
                selectedTextColor = NavyPrimary
            )
        )

        NavigationBarItem(
            selected = currentRoute == NavRoutes.SETTINGS,
            onClick = { onNavigate(NavRoutes.SETTINGS) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavyPrimary,
                selectedTextColor = NavyPrimary
            )
        )
    }
}
