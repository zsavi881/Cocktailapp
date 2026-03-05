package fr.isen.savi.thegreatestcocktailapp.ecran

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import fr.isen.savi.thegreatestcocktailapp.network.RetrofitInstance
import kotlin.coroutines.cancellation.CancellationException
import androidx.compose.ui.graphics.Brush

@Composable
fun RandomScreen(
    onCocktailClick: (String) -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    var idDrink by remember { mutableStateOf<String?>(null) }
    var name by remember { mutableStateOf<String?>(null) }
    var thumb by remember { mutableStateOf<String?>(null) }
    var category by remember { mutableStateOf<String?>(null) }

    var refreshKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(refreshKey) {
        isLoading = true
        error = null
        try {
            val res = RetrofitInstance.api.getRandomCocktail()
            val d = res.drinks?.firstOrNull()
            if (d == null) {
                error = "No drink returned"
                idDrink = null
                name = null
                thumb = null
                category = null
            } else {
                idDrink = d.idDrink
                name = d.strDrink
                thumb = d.strDrinkThumb
                category = d.strCategory
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            error = e.message ?: "Network error"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            error != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Error: $error",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Button(
                            onClick = { refreshKey++ },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Retry") }
                    }
                }
            }

            else -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val id = idDrink
                            if (id != null) onCocktailClick(id)
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {

                        // Badge catégorie au-dessus de l'image
                        if (!category.isNullOrBlank()) {
                            Surface(
                                shape = MaterialTheme.shapes.large,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = category ?: "",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }

                            Spacer(Modifier.height(8.dp))
                        }

                        if (!thumb.isNullOrBlank()) {
                            AsyncImage(
                                model = thumb,
                                contentDescription = name ?: "Cocktail",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .clip(MaterialTheme.shapes.large)
                            )

                            Spacer(Modifier.height(10.dp))
                        }

                        Text(
                            text = name ?: "Unknown cocktail",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            "Tap to open details",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Button(
            onClick = { refreshKey++ },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get another random")
        }
    }
}