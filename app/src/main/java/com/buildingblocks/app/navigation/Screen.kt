package com.buildingblocks.app.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Home : Screen("home")
    object Collection : Screen("collection")
    object Backlog : Screen("backlog")
    object Storage : Screen("storage")   // main bottom-nav tab
    object Profile : Screen("profile")   // reachable from top-bar avatar
    object AddSet : Screen("add_set")

    object SetDetails : Screen("set_details/{setId}") {
        const val ROUTE = "set_details/{setId}"
        fun createRoute(setId: String) = "set_details/$setId"
    }

    object EditSet : Screen("edit_set/{setId}") {
        const val ROUTE = "edit_set/{setId}"
        fun createRoute(setId: String) = "edit_set/$setId"
    }

    object Premium : Screen("premium")
}
