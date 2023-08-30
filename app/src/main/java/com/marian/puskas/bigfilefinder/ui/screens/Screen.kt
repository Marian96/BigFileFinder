package com.marian.puskas.bigfilefinder.ui.screens

sealed class Screen(val route: String) {
    object MainScreen: Screen("main_screen")
    object DirectoriesScreen: Screen("directoriesScreen")
    object SearchResultsScreen: Screen("search_results_screen")
}