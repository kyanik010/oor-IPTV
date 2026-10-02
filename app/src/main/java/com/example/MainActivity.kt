package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.ActivationLoginScreen
import com.example.ui.screens.EpgScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveTvScreen
import com.example.ui.screens.MovieDetailScreen
import com.example.ui.screens.MoviesScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.ProfilesScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SeriesDetailScreen
import com.example.ui.screens.SeriesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.NoorIptvTheme
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.ScreenRoute

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NoorIptvTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()

                BackHandler(enabled = currentScreen != ScreenRoute.Home && currentScreen != ScreenRoute.Splash) {
                    viewModel.navigateBack()
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkNavyBg)
                        .then(
                            // Player screen runs full-bleed without padding insets
                            if (currentScreen == ScreenRoute.Player) Modifier else Modifier.safeDrawingPadding()
                        )
                ) {
                    when (currentScreen) {
                        is ScreenRoute.Splash -> {
                            SplashScreen(
                                onNavigateNext = {
                                    viewModel.navigateTo(ScreenRoute.Home)
                                }
                            )
                        }
                        is ScreenRoute.Activation -> {
                            ActivationLoginScreen(viewModel = viewModel)
                        }
                        is ScreenRoute.Profiles -> {
                            ProfilesScreen(viewModel = viewModel)
                        }
                        is ScreenRoute.Home -> {
                            HomeScreen(viewModel = viewModel)
                        }
                        is ScreenRoute.LiveTv -> {
                            LiveTvScreen(viewModel = viewModel)
                        }
                        is ScreenRoute.Movies -> {
                            MoviesScreen(viewModel = viewModel)
                        }
                        is ScreenRoute.MovieDetail -> {
                            MovieDetailScreen(viewModel = viewModel)
                        }
                        is ScreenRoute.Series -> {
                            SeriesScreen(viewModel = viewModel)
                        }
                        is ScreenRoute.SeriesDetail -> {
                            SeriesDetailScreen(viewModel = viewModel)
                        }
                        is ScreenRoute.Epg -> {
                            EpgScreen(viewModel = viewModel)
                        }
                        is ScreenRoute.Search -> {
                            SearchScreen(viewModel = viewModel)
                        }
                        is ScreenRoute.Favorites -> {
                            com.example.ui.screens.FavoritesScreen(viewModel = viewModel)
                        }
                        is ScreenRoute.Settings -> {
                            SettingsScreen(viewModel = viewModel)
                        }
                        is ScreenRoute.Player -> {
                            PlayerScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
