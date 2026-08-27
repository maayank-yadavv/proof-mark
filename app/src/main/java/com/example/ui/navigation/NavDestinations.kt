package com.example.ui.navigation

sealed class Screen(val route: String, val title: String) {
    data object Login : Screen("login", "Officer Sign-In")
    data object Register : Screen("register", "Officer Enrolment")
    data object Dashboard : Screen("dashboard", "Dashboard")
    data object Camera : Screen("camera", "Live Label Scanner")
    data object NewInspection : Screen("new_inspection", "New Inspection")
    data object Processing : Screen("processing", "Processing")
    data object Results : Screen("results/{inspectionId}", "Compliance Results") {
        fun createRoute(inspectionId: String) = "results/$inspectionId"
    }
    data object Evidence : Screen("evidence/{inspectionId}", "Evidence Inspector") {
        fun createRoute(inspectionId: String) = "evidence/$inspectionId"
    }
    data object HumanReview : Screen("review/{inspectionId}", "Human Review") {
        fun createRoute(inspectionId: String) = "review/$inspectionId"
    }
    data object Report : Screen("report/{inspectionId}", "Notice & Report") {
        fun createRoute(inspectionId: String) = "report/$inspectionId"
    }
    data object History : Screen("history", "History")
    data object Rules : Screen("rules", "Rules Registry")
    data object AuditLogs : Screen("audit_logs", "Audit Logs")
    data object DatabaseInspector : Screen("database_inspector", "Room DB Inspector")
    data object FssaiDatabase : Screen("fssai_database", "FSSAI Register")
    data object Settings : Screen("settings", "Settings & RBAC")
}
