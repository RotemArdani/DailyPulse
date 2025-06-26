package com.colman.dailypulse.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.colman.dailypulse.features.auth.SignInScreen
import com.colman.dailypulse.features.auth.SignUpScreen
import com.colman.dailypulse.features.habits.CreateHabitScreen
import com.colman.dailypulse.features.habits.Habits
import com.colman.dailypulse.features.posts.CreatePostScreen
import com.colman.dailypulse.features.posts.PostsScreen
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val user = remember { FirebaseAuth.getInstance().currentUser }
    val startDestination = if (user != null) {
        AppDestinations.POSTS_ROUTE
    } else {
        AppDestinations.SIGN_IN_ROUTE
    }

    val invisibleBottomBar = currentRoute in listOf(
        AppDestinations.SIGN_IN_ROUTE,
        AppDestinations.SIGN_UP_ROUTE
    )

    Scaffold(
        bottomBar = {
            if (!invisibleBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == AppDestinations.POSTS_ROUTE,
                        onClick = {
                            navController.navigate(AppDestinations.POSTS_ROUTE) {
                                popUpTo(AppDestinations.POSTS_ROUTE) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.MailOutline, null) },
                        label = { Text("Posts") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == AppDestinations.HABITS_ROUTE,
                        onClick = {
                            navController.navigate(AppDestinations.HABITS_ROUTE) {
                                popUpTo(AppDestinations.POSTS_ROUTE) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.Create, null) },
                        label = { Text("Habits") }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {
            composable(AppDestinations.SIGN_IN_ROUTE) {
                SignInScreen(
                    onSignInSuccess = {
                        navController.navigate(AppDestinations.POSTS_ROUTE) {
                            popUpTo(AppDestinations.SIGN_IN_ROUTE) { inclusive = true }
                        }
                    },
                    onNavigateToSignUp = {
                        navController.navigate(AppDestinations.SIGN_UP_ROUTE)
                    }
                )
            }

            composable(AppDestinations.SIGN_UP_ROUTE) {
                SignUpScreen(
                    onSignUpSuccess = {
                        navController.navigate(AppDestinations.SIGN_IN_ROUTE) {
                            popUpTo(AppDestinations.SIGN_UP_ROUTE) { inclusive = true }
                        }
                    },
                    onNavigateToSignIn = {
                        navController.popBackStack()
                    }
                )
            }

            composable(AppDestinations.POSTS_ROUTE) {
                PostsScreen(
                    onCreatePostClick = {
                        navController.navigate(AppDestinations.CREATE_POST_ROUTE)
                    }
                )
            }

            composable(AppDestinations.HABITS_ROUTE) {
                Habits(
                    onCreateHabitClick = {
                        navController.navigate(AppDestinations.CREATE_HABIT_ROUTE)
                    }
                )
            }

            composable(AppDestinations.CREATE_POST_ROUTE) {
                CreatePostScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }

            composable(AppDestinations.CREATE_HABIT_ROUTE) {
                CreateHabitScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}