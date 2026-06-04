package com.example.electronicreception
import com.example.electronicreception.screens.NewsScreen
import com.example.electronicreception.screens.HelpScreen
import com.example.electronicreception.screens.ContactsScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.electronicreception.data.FirebaseRepository
import com.example.electronicreception.screens.AdminScreen
import com.example.electronicreception.screens.HomeScreen
import com.example.electronicreception.screens.LoginScreen
import com.example.electronicreception.screens.MyComplaintsScreen
import com.example.electronicreception.screens.NewComplaintScreen
import com.example.electronicreception.screens.ProfileScreen
import com.example.electronicreception.screens.ComplaintDetailsScreen

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val NEW_COMPLAINT = "newComplaint"
    const val MY_COMPLAINTS = "myComplaints"
    const val PROFILE = "profile"
    const val ADMIN = "admin"

    const val COMPLAINT_DETAILS = "complaintDetails"
    const val NEWS = "news"
    const val HELP = "help"
    const val CONTACTS = "contacts"

}

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val repository = remember { FirebaseRepository() }

        NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                repository = repository,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Routes.NEWS) {
            NewsScreen(
                repository = repository,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.HELP) {
            HelpScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.CONTACTS) {
            ContactsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(

                repository = repository,
                onNewComplaint = {
                    navController.navigate(Routes.NEW_COMPLAINT)
                },
                onMyComplaints = {
                    navController.navigate(Routes.MY_COMPLAINTS)
                },
                onProfile = {
                    navController.navigate(Routes.PROFILE)
                },
                onNews = {
                    navController.navigate(Routes.NEWS)
                },
                onHelp = {
                    navController.navigate(Routes.HELP)
                },
                onContacts = {
                    navController.navigate(Routes.CONTACTS)
                },
                onAdmin = {
                    navController.navigate(Routes.ADMIN)
                },
                onLogout = {
                    repository.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                }

            )
        }

        composable(Routes.NEW_COMPLAINT) {
            NewComplaintScreen(
                repository = repository,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

            composable(Routes.MY_COMPLAINTS) {
                MyComplaintsScreen(
                    repository = repository,
                    onBack = {
                        navController.popBackStack()
                    },
                    onComplaintClick = { complaintId ->
                        navController.navigate("${Routes.COMPLAINT_DETAILS}/$complaintId")
                    }
                )
            }
            composable("${Routes.COMPLAINT_DETAILS}/{complaintId}") { backStackEntry ->
                val complaintId = backStackEntry.arguments?.getString("complaintId") ?: ""

                ComplaintDetailsScreen(
                    repository = repository,
                    complaintId = complaintId,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

        composable(Routes.PROFILE) {
            ProfileScreen(
                repository = repository,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.ADMIN) {
            AdminScreen(
                repository = repository,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}