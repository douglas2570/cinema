import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cinema.ui.components.IconButtonCinema
import com.example.cinema.ui.screens.details.DetailsScreen
import com.example.cinema.ui.screens.details.DetailsViewModel
import com.example.cinema.ui.screens.favorites.FavoritesScreen
import com.example.cinema.ui.screens.home.HomeScreen
import com.example.cinema.ui.screens.home.MoviesViewModel
import com.example.cinema.ui.theme.DarkBlue
import com.example.cinema.ui.theme.Primary
import com.example.cinema.ui.theme.Red
import com.example.cinema.ui.theme.Secondary
import com.example.cinema.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CinemaApp() {
    val rotas = listOf("Home")
    var currentIndex by remember { mutableStateOf(0) }
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        modifier = Modifier.clip(RectangleShape),
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                LazyColumn(modifier = Modifier
                    .background(Primary)
                    .fillMaxHeight()
                    .fillMaxWidth(0.8f)
                ) {
                    itemsIndexed(rotas){index, item ->
                        NavigationDrawerItem(
                            label = { Text(text = item)},
                            selected = currentIndex == index,
                            onClick = {
                                navController.navigate(item)
                                currentIndex = index
                                scope.launch {
                                    drawerState.apply {
                                        close()
                                    }
                                }
                            },
                            colors =
                            NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = DarkBlue,
                                unselectedContainerColor = Primary,
                                selectedTextColor = White,
                                unselectedTextColor = White
                            ),
                            shape = RoundedCornerShape(16.dp)

                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = { CinemaTopAppBar(scrollBehavior, scope, drawerState, navController) },
        ){
            Column(
                Modifier
                    .padding(it)
                    .background(Secondary))
            {
                NavHost(navController, startDestination = "home") {
                    composable("home") {
                        val moviesViewModel:MoviesViewModel = viewModel<MoviesViewModel>()

                        HomeScreen(
                            modifier = Modifier,
                            moviesUiState = moviesViewModel.moviesUiState,
                            navController,
                            moviesViewModel
                        )
                    }
                    composable("movies") {
                        //MoviesScreen()

                    }
                    composable("favorites") {
                        FavoritesScreen()
                    }
                    composable("details/{movieId}"){ backstackEntry ->
                        backstackEntry.arguments?.getString("movieId")
                            ?.let { movieId ->
                                val viewModel = viewModel<DetailsViewModel>(
                                    factory = object :ViewModelProvider.Factory{
                                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                            return DetailsViewModel(movieId.toInt()) as T
                                        }
                                    }
                                )
                                DetailsScreen(viewModel.movieUiState)
                            }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CinemaTopAppBar(scrollBehavior: TopAppBarScrollBehavior, scope: CoroutineScope, drawerState: DrawerState, navController: NavController) {

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Primary,
            titleContentColor = White,
        ),
        title = {
            IconButtonCinema(Icons.Filled.Home, "Home", White, { navController.navigate("home") })
        },
        navigationIcon = {
            IconButtonCinema(Icons.Filled.Menu, "Menu", White) {
                scope.launch {
                    drawerState.apply {
                        if (isClosed) open() else close()
                    }
                }

            }
        },
        actions = {
            IconButtonCinema(Icons.Filled.Favorite, "Favorite", Red,{ navController.navigate("favorites") })
        },
        scrollBehavior = scrollBehavior,
    )

}

@Preview(showBackground = true)
@Composable
fun PreviewApp(){
    CinemaApp()
}

