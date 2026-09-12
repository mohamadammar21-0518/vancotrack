package my.edu.aiu.app.tdminsight.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import my.edu.aiu.app.tdminsight.ui.screens.CalculatorScreen
import my.edu.aiu.app.tdminsight.ui.screens.CameraScreen
import my.edu.aiu.app.tdminsight.ui.screens.ExplanationScreen
import my.edu.aiu.app.tdminsight.ui.screens.HistoryScreen
import my.edu.aiu.app.tdminsight.ui.screens.LabReportReviewScreen
import my.edu.aiu.app.tdminsight.ui.screens.ResultsScreen
import my.edu.aiu.app.tdminsight.ui.screens.SplashScreen
import my.edu.aiu.app.tdminsight.viewmodel.TdmViewModel

@Composable
fun TdmNavigation() {
    val navController = rememberNavController()
    val viewModel     = viewModel<TdmViewModel>()

    NavHost(
        navController       = navController,
        startDestination    = "splash",
        enterTransition     = { fadeIn(tween(300)) },
        exitTransition      = { fadeOut(tween(200)) },
        popEnterTransition  = { fadeIn(tween(300)) },
        popExitTransition   = { fadeOut(tween(200)) },
    ) {

        // ── Splash ───────────────────────────────────────────────────────────
        composable(
            route = "splash",
            exitTransition = {
                slideOutOfContainer(
                    towards       = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350),
                )
            },
        ) {
            SplashScreen(
                onGetStarted = {
                    navController.navigate("calculator") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
            )
        }

        // ── Calculator ───────────────────────────────────────────────────────
        composable(
            route = "calculator",
            enterTransition = {
                slideIntoContainer(
                    towards       = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350),
                )
            },
            popEnterTransition = { fadeIn(tween(300)) },
        ) {
            CalculatorScreen(
                viewModel    = viewModel,
                onCalculated = { navController.navigate("results") },
                onOpenCamera = { targetField ->
                    viewModel.startCamera(targetField)
                    navController.navigate("camera")
                },
                onOpenHistory = { navController.navigate("history") },
            )
        }

        // ── History ──────────────────────────────────────────────────────────
        composable(
            route = "history",
            enterTransition = {
                slideIntoContainer(
                    towards       = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300),
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards       = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300),
                )
            },
        ) {
            HistoryScreen(
                vm     = viewModel,
                onBack = { navController.popBackStack() },
            )
        }

        // ── Camera ───────────────────────────────────────────────────────────
        composable(
            route = "camera",
            enterTransition = {
                slideIntoContainer(
                    towards       = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(350),
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards       = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(300),
                )
            },
        ) {
            val state by viewModel.state.collectAsState()
            CameraScreen(
                targetField     = state.cameraTargetField,
                onImageCaptured = { uri ->
                    viewModel.onImageCaptured(uri)
                    navController.navigate("review") {
                        popUpTo("camera") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }

        // ── Lab Report Review ────────────────────────────────────────────────
        composable(
            route = "review",
            enterTransition = {
                slideIntoContainer(
                    towards       = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300),
                )
            },
        ) {
            LabReportReviewScreen(
                vm          = viewModel,
                onConfirmed = {
                    // Pop all the way back to calculator, value already injected
                    navController.popBackStack("calculator", inclusive = false)
                },
                onRetake    = {
                    // Go back to camera screen
                    navController.navigate("camera") {
                        popUpTo("review") { inclusive = true }
                    }
                },
                onBack      = { navController.popBackStack() },
            )
        }

        // ── Results ──────────────────────────────────────────────────────────
        composable(
            route = "results",
            enterTransition = {
                slideIntoContainer(
                    towards       = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350),
                )
            },
        ) {
            ResultsScreen(
                vm        = viewModel,
                onExplain = { navController.navigate("explanation") },
                onReset   = {
                    viewModel.reset()
                    navController.popBackStack("calculator", inclusive = false)
                },
            )
        }

        // ── Explanation ──────────────────────────────────────────────────────
        composable(
            route = "explanation",
            enterTransition = {
                slideIntoContainer(
                    towards       = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(350),
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards       = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(300),
                )
            },
        ) {
            ExplanationScreen(viewModel) { navController.popBackStack() }
        }
    }
}
