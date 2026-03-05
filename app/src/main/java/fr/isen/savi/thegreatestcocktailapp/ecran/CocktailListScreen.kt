package fr.isen.savi.thegreatestcocktailapp.ecran

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import fr.isen.savi.thegreatestcocktailapp.network.RetrofitInstance

@Composable
fun CocktailListScreen(
    category: String,
    onCocktailClick: (String) -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var drinks by remember { mutableStateOf<List<AnyCocktail>>(emptyList()) }

    LaunchedEffect(category) {
        isLoading = true
        error = null
        runCatching {
            RetrofitInstance.api.getDrinksByCategory(category)
        }.onSuccess { res ->
            drinks = (res.drinks ?: emptyList()).map {
                AnyCocktail(
                    id = it.idDrink,
                    name = it.strDrink,
                    thumb = it.strDrinkThumb
                )
            }
        }.onFailure { e ->
            error = e.message ?: "Erreur réseau"
        }
        isLoading = false
    }

    when {
        isLoading -> {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        error != null -> {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Error: $error",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(drinks) { c ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCocktailClick(c.id) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AsyncImage(
                                model = c.thumb,
                                contentDescription = c.name,
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = c.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class AnyCocktail(
    val id: String,
    val name: String,
    val thumb: String
)