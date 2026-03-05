package fr.isen.savi.thegreatestcocktailapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import fr.isen.savi.thegreatestcocktailapp.ecran.CategoriesScreen
import fr.isen.savi.thegreatestcocktailapp.ecran.CocktailListScreen
import fr.isen.savi.thegreatestcocktailapp.ecran.DetailCocktailScreen
import fr.isen.savi.thegreatestcocktailapp.ecran.RandomScreen
import fr.isen.savi.thegreatestcocktailapp.ecran.SearchScreen
import fr.isen.savi.thegreatestcocktailapp.ui.FavoritesScreen
import fr.isen.savi.thegreatestcocktailapp.ui.theme.TheGreatestCocktailAppTheme


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TheGreatestCocktailAppTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route ?: "random"

                val context = LocalContext.current
                var favoriteIds by remember { mutableStateOf<Set<String>>(emptySet()) }
                val favoritesStore =
                    remember { fr.isen.savi.thegreatestcocktailapp.data.FavoritesStore(context) }
                val currentId = backStackEntry?.arguments?.getString("id") // route: detail/{id}

                // "Coeur" qui change de couleur : ici il est "actif" quand on est sur Favorites
                val heartActive = currentRoute.startsWith("favorites")

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("The Greatest Cocktail App") },
                            actions = {
                                if (currentRoute == "detail/{id}" && currentId != null) {
                                    val isFav = favoriteIds.contains(currentId)
                                    IconButton(
                                        onClick = {
                                            favoriteIds = favoritesStore.toggle(currentId)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                            contentDescription = "Favorite",
                                            tint = if (isFav) Color.Red else LocalContentColor.current
                                        )
                                    }
                                }
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            val items = listOf(
                                BottomItem("random", "Random", Icons.Filled.Shuffle),
                                BottomItem("categories", "List", Icons.Filled.List),
                                BottomItem("favorites", "Favorites", Icons.Filled.Favorite),
                                BottomItem("search", "Search", Icons.Filled.Search)
                            )

                            items.forEach { item ->
                                val selected =
                                    currentRoute == item.route || currentRoute.startsWith(item.route)

                                NavigationBarItem(
                                    selected = selected,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            launchSingleTop = true
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.label
                                        )
                                    },
                                    label = { Text(item.label) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "random",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("favorites") {
                            FavoritesScreen(
                                onCocktailClick = { id ->
                                    navController.navigate("detail/$id")
                                }
                            )
                        }

                        composable("search") {
                            SearchScreen(navController = navController)
                        }

                        composable("random") {
                            RandomScreen(
                                onCocktailClick = { id ->
                                    navController.navigate("detail/$id")
                                }
                            )
                        }

                        composable("categories") {
                            CategoriesScreen(
                                onCategoryClick = { category ->
                                    val encoded = URLEncoder.encode(
                                        category,
                                        StandardCharsets.UTF_8.toString()
                                    )
                                    navController.navigate("cocktails/$encoded")
                                }
                            )
                        }


                        composable(
                            route = "cocktails/{category}",
                            arguments = listOf(navArgument("category") {
                                type = NavType.StringType
                            })
                        ) { entry ->
                            val encoded = entry.arguments?.getString("category").orEmpty()
                            val category = java.net.URLDecoder.decode(encoded, "UTF-8")

                            CocktailListScreen(
                                category = category,
                                onCocktailClick = { id ->
                                    navController.navigate("detail/$id")
                                }
                            )
                        }

                        composable(
                            route = "detail/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.StringType })
                        ) { entry ->
                            val id = entry.arguments?.getString("id").orEmpty()
                            DetailCocktailScreen(cocktailId = id)
                        }
                    }
                }
            }
        }
    }
}

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
