package com.colman.dailypulse.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.colman.dailypulse.CreateHabitScreen
import com.colman.dailypulse.features.user.UserViewModel // Assuming UserViewModel holds auth state
// Import your main app screen Composable, e.g., HabitsScreen or a MainAppScreen
import com.colman.dailypulse.HabitsScreen // Replace with your actual main screen
import com.colman.dailypulse.SignInScreen
import com.colman.dailypulse.SignUpScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    // userViewModel: UserViewModel = hiltViewModel() // Or koinViewModel()
) {
    val navController = rememberNavController()

    // Example: Determine start destination based on login state (optional)
    // val isLoggedIn by userViewModel.isLoggedInState.collectAsState() // You'd need to create this in UserViewModel
    // val startDestination = if (isLoggedIn) AppDestinations.MAIN_APP_CONTENT_ROUTE else AppDestinations.SIGN_IN_ROUTE
    // For simplicity, we'll start at SignIn for now. Implement conditional start later if needed. [1]

    val startDestination = AppDestinations.SIGN_IN_ROUTE // Default to sign-in

    NavHost(navController = navController, startDestination = startDestination) {
        composable(AppDestinations.SIGN_IN_ROUTE) {
            SignInScreen(
                onSignInSuccess = {
                    // Navigate to main app content, clear back stack to prevent going back to sign-in
                    navController.navigate(AppDestinations.CREATE_HABIT_ROUTE) {
                        popUpTo(AppDestinations.SIGN_IN_ROUTE) { inclusive = true }
                        launchSingleTop = true
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
                    // Often, after sign-up, you navigate to sign-in or directly to the app
                    // For this example, let's go to sign-in
                    navController.navigate(AppDestinations.SIGN_IN_ROUTE) {
                        popUpTo(AppDestinations.SIGN_UP_ROUTE) { inclusive = true } // Remove sign-up from back stack
                        launchSingleTop = true
                    }
                    // Or, if sign-up automatically signs the user in:
                    // navController.navigate(AppDestinations.MAIN_APP_CONTENT_ROUTE) {
                    // popUpTo(AppDestinations.SIGN_UP_ROUTE) { inclusive = true }
                    // }
                },
                onNavigateToSignIn = {
                    navController.popBackStack() // Simply go back to the previous screen (SignIn)
                }
            )
        }
        composable(AppDestinations.MAIN_APP_CONTENT_ROUTE) {
            // This is where your main application content goes after login.
            // Replace HabitsScreen with your actual main app screen/dashboard.
            HabitsScreen(
                // Pass necessary parameters or ViewModels
                // onNavigateToProfile = { navController.navigate("profile") }, // Example
                // onLogout = {
                //    userViewModel.logout()
                //    navController.navigate(AppDestinations.SIGN_IN_ROUTE) {
                //        popUpTo(AppDestinations.MAIN_APP_CONTENT_ROUTE) { inclusive = true }
                //    }
                // }
            )
        }
        composable(AppDestinations.CREATE_HABIT_ROUTE) {
            // This is where your main application content goes after login.
            // Replace HabitsScreen with your actual main app screen/dashboard.
            CreateHabitScreen(
               onNavigateBack = {
                   navController.navigate(AppDestinations.MAIN_APP_CONTENT_ROUTE)
               }
//                viewModel = TODO(),
//                onHabitCreatedSuccessfully = TODO(),
//                onNavigateBack = TODO(),

                // Pass necessary parameters or ViewModels
                // onNavigateToProfile = { navController.navigate("profile") }, // Example
                // onLogout = {
                //    userViewModel.logout()
                //    navController.navigate(AppDestinations.SIGN_IN_ROUTE) {
                //        popUpTo(AppDestinations.MAIN_APP_CONTENT_ROUTE) { inclusive = true }
                //    }
                // }
            )
        }
        // Add other destinations (e.g., profile, settings) here
    }
}