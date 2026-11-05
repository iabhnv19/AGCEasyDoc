package com.example.agceasydoc

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object CustomizeDoc : Screen("customize_doc")
    object PrintPreview : Screen("print_preview")
    object RecentDocuments : Screen("recent_documents")
    object Settings : Screen("settings")
}
