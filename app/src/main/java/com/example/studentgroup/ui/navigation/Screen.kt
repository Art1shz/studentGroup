package com.example.studentgroup.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Registration : Screen("registration")
    object ForgotPassword : Screen("forgot_password")
    object Main : Screen("main")
    object Schedule : Screen("schedule")
    object GroupList : Screen("group_list")
    object Tasks : Screen("tasks")
    object Profile : Screen("profile")
} 