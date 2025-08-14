package com.colman.dailypulse.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.colman.dailypulse.features.auth.SignInScreen
import com.colman.dailypulse.features.auth.SignUpScreen
import com.colman.dailypulse.features.habits.CreateHabitScreen
import com.colman.dailypulse.features.habits.EditHabitScreen
import com.colman.dailypulse.features.habits.Habits
import com.colman.dailypulse.features.posts.CreatePostScreen
import com.colman.dailypulse.features.posts.PostsScreen
import com.colman.dailypulse.ui.components.InsideAppHeader
import com.colman.dailypulse.utils.LocalSnackbarController
import com.colman.dailypulse.utils.SnackbarController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarController = remember { SnackbarController(snackbarHostState) }

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
    CompositionLocalProvider(LocalSnackbarController provides snackbarController) {

    Scaffold(
        topBar = {
            if (!invisibleBottomBar) {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            InsideAppHeader()
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate(AppDestinations.SIGN_IN_ROUTE) {
                                snackbarController.showMessage("Successfully logged out")
                                popUpTo(0) { inclusive = true }
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                        }
                    }
                )
            }
        },
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
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = data.visuals.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp)
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
                        navController.navigate(AppDestinations.POSTS_ROUTE) {
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

            composable(AppDestinations.CREATE_POST_ROUTE) {
                CreatePostScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate(AppDestinations.POSTS_ROUTE) {
                            popUpTo(AppDestinations.POSTS_ROUTE) { inclusive = true }
                        }
                    }
                )
            }

            composable(AppDestinations.HABITS_ROUTE) {
                Habits(
                    onCreateHabitClick = {
                        navController.navigate(AppDestinations.CREATE_HABIT_ROUTE)
                    },
                    onEditHabitClick = { habit ->
                        navController.navigate("${AppDestinations.EDIT_HABIT_ROUTE}/${habit.id}")
                    },
                    onNavigateBack = {
                        navController.navigate(AppDestinations.HABITS_ROUTE) {
                            popUpTo(AppDestinations.HABITS_ROUTE) { inclusive = true }
                        }
                    }
                )
            }


            composable(AppDestinations.CREATE_HABIT_ROUTE) {
                CreateHabitScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate(AppDestinations.HABITS_ROUTE) {
                            popUpTo(AppDestinations.HABITS_ROUTE) { inclusive = true }
                        }
                    }
                )
            }

            composable("${AppDestinations.EDIT_HABIT_ROUTE}/{habitId}") { backStackEntry ->
                val habitId = backStackEntry.arguments?.getString("habitId")

                EditHabitScreen(
                    habitId = habitId?: "",
                    onNavigateBack = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate(AppDestinations.HABITS_ROUTE) {
                            popUpTo(AppDestinations.HABITS_ROUTE) { inclusive = true }
                        }
                    }
                )
            }

        }
    }
    }
}