package fr.isen.savi.thegreatestcocktailapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import fr.isen.savi.thegreatestcocktailapp.data.FavoritesStore
import fr.isen.savi.thegreatestcocktailapp.network.RetrofitInstance
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

@Composable
fun FavoritesScreen(
    onCocktailClick: (String) -> Unit
) {
    val context = LocalContext.current
    val store = remember { FavoritesStore(context) }

    var favoriteIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    var cocktails by remember { mutableStateOf<List<Pair<String, String?>>>(emptyList()) }
    // Pair(id, thumb) + on affiche aussi le nom dans la même structure ci-dessous
    var cocktailNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    fun reloadIds() {
        favoriteIds = store.getAll()
    }

    suspend fun loadDetails(ids: Set<String>) {
        isLoading = true
        error = null
        try {
            val results = coroutineScope {
                ids.toList().map { id ->
                    async {
                        val res = RetrofitInstance.api.cocktailById(id)
                        val d = res.drinks?.firstOrNull()
                        Triple(id, d?.strDrink ?: "unknown", d?.strDrinkThumb)
                    }
                }.awaitAll()
            }

            cocktailNames = results.associate { it.first to it.second }
            cocktails = results.map { it.first to it.third }
        } catch (e: Exception) {
            error = e.message ?: "Erreur réseau"
            cocktails = emptyList()
            cocktailNames = emptyMap()
        } finally {
            isLoading = false
        }
    }

    // Recharge quand on revient sur l’écran (après toggle dans le detail)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) reloadIds()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) { reloadIds() }
    LaunchedEffect(favoriteIds) {
        if (favoriteIds.isNotEmpty()) loadDetails(favoriteIds) else {
            cocktails = emptyList()
            cocktailNames = emptyMap()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Likes",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Tes cocktails favoris",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // (4) Badge bleu du nombre de favoris
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            ) {
                Text(
                    text = "${favoriteIds.size} cocktails",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            error != null -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Erreur",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = error ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            favoriteIds.isEmpty() -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Aucun cocktail liké.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Ajoute-en depuis la page détail.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(cocktails, key = { it.first }) { (id, thumb) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCocktailClick(id) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = MaterialTheme.shapes.large,
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // (2) Pastille bleue derrière l’image
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = thumb,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                    )
                                }

                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = cocktailNames[id] ?: "unknown",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = "ID: $id",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}