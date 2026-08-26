package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.models.UserRole
import com.example.ui.navigation.Screen
import com.example.ui.screens.audit.AuditLogsScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.database.DatabaseInspectorScreen
import com.example.ui.screens.evidence.EvidenceInspectorScreen
import com.example.ui.screens.history.InspectionHistoryScreen
import com.example.ui.screens.report.InspectionReportScreen
import com.example.ui.screens.results.ComplianceResultsScreen
import com.example.ui.screens.review.HumanReviewScreen
import com.example.ui.screens.rules.RuleManagementScreen
import com.example.ui.screens.scan.CameraScreen
import com.example.ui.screens.scan.NewInspectionScreen
import com.example.ui.screens.scan.ProcessingPipelineScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.ProofMarkTheme
import com.example.ui.viewmodel.InspectionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProofMarkTheme {
                ProofMarkApp()
            }
        }
    }
}

@Composable
fun ProofMarkApp() {
    val navController = rememberNavController()
    val viewModel: InspectionViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isStandardUser = currentUser.role == UserRole.STANDARD_USER

    val bottomNavItems = if (isStandardUser) {
        listOf(
            Triple(Screen.Dashboard.route, "Scan & Home", Icons.Default.QrCodeScanner),
            Triple(Screen.History.route, "My Scans", Icons.Default.History),
            Triple(Screen.Settings.route, "Account", Icons.Default.Settings)
        )
    } else {
        listOf(
            Triple(Screen.Dashboard.route, "Dashboard", Icons.Default.Dashboard),
            Triple(Screen.History.route, "Records", Icons.Default.History),
            Triple(Screen.Rules.route, "Rules", Icons.AutoMirrored.Filled.MenuBook),
            Triple(Screen.Settings.route, "Settings", Icons.Default.Settings)
        )
    }

    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.History.route,
        Screen.Rules.route,
        Screen.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(modifier = Modifier.testTag("main_bottom_nav")) {
                    bottomNavItems.forEach { (route, label, icon) ->
                        val selected = currentRoute == route
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            selected = selected,
                            onClick = {
                                if (currentRoute != route) {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onRegistrationSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateNewInspection = { navController.navigate(Screen.NewInspection.route) },
                    onNavigateInspectionDetail = { id -> navController.navigate(Screen.Results.createRoute(id)) },
                    onNavigateHistory = { navController.navigate(Screen.History.route) },
                    onNavigateRules = { navController.navigate(Screen.Rules.route) },
                    onNavigateSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateCamera = { navController.navigate(Screen.Camera.route) }
                )
            }

            composable(Screen.Camera.route) {
                CameraScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onInspectionCompleted = { id ->
                        navController.navigate(Screen.Results.createRoute(id)) {
                            popUpTo(Screen.Dashboard.route)
                        }
                    }
                )
            }

            composable(Screen.NewInspection.route) {
                NewInspectionScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onInspectionStarted = { id ->
                        navController.navigate(Screen.Results.createRoute(id)) {
                            popUpTo(Screen.Dashboard.route)
                        }
                    },
                    onNavigateCamera = { navController.navigate(Screen.Camera.route) }
                )
            }

            composable(Screen.Processing.route) {
                ProcessingPipelineScreen(
                    viewModel = viewModel
                )
            }

            composable(
                route = Screen.Results.route,
                arguments = listOf(navArgument("inspectionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("inspectionId") ?: ""
                ComplianceResultsScreen(
                    inspectionId = id,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateEvidence = { inspectionId -> navController.navigate(Screen.Evidence.createRoute(inspectionId)) },
                    onNavigateReview = { inspectionId -> navController.navigate(Screen.HumanReview.createRoute(inspectionId)) },
                    onNavigateReport = { inspectionId -> navController.navigate(Screen.Report.createRoute(inspectionId)) }
                )
            }

            composable(
                route = Screen.Evidence.route,
                arguments = listOf(navArgument("inspectionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("inspectionId") ?: ""
                EvidenceInspectorScreen(
                    inspectionId = id,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.HumanReview.route,
                arguments = listOf(navArgument("inspectionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("inspectionId") ?: ""
                HumanReviewScreen(
                    inspectionId = id,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onReviewCompleted = { navController.navigate(Screen.Report.createRoute(id)) }
                )
            }

            composable(
                route = Screen.Report.route,
                arguments = listOf(navArgument("inspectionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("inspectionId") ?: ""
                InspectionReportScreen(
                    inspectionId = id,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.History.route) {
                InspectionHistoryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateInspectionDetail = { id -> navController.navigate(Screen.Results.createRoute(id)) }
                )
            }

            composable(Screen.Rules.route) {
                RuleManagementScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AuditLogs.route) {
                AuditLogsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.DatabaseInspector.route) {
                DatabaseInspectorScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateAuditLogs = { navController.navigate(Screen.AuditLogs.route) },
                    onNavigateDatabaseInspector = { navController.navigate(Screen.DatabaseInspector.route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
