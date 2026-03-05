package fr.isen.savi.thegreatestcocktailapp.ecran

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color// pour les couleurs
import fr.isen.savi.thegreatestcocktailapp.network.RetrofitInstance
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.padding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailCocktailScreen(
    /*modifier: Modifier = Modifier,
    cocktailId: String*/
    cocktailId: String
) {
    val context = LocalContext.current
    var isFavorite by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("Loading...") }

    var thumb by remember { mutableStateOf<String?>(null) }

    //pour remplir les champs
    var instructions by remember { mutableStateOf<String?>(null) }
    var ingredients by remember { mutableStateOf<List<String>>(emptyList()) }
    var category by remember { mutableStateOf<String?>(null) }
    var glass by remember { mutableStateOf<String?>(null) }
    var alcoholic by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(cocktailId) {
        try {
            val res = RetrofitInstance.api.cocktailById(cocktailId)
            val d = res.drinks?.firstOrNull()
            if (d != null) {
                name = d.strDrink ?: "unknow"
                thumb = d.strDrinkThumb
                category = d.strCategory
                glass = d.strGlass
                instructions = d.strInstructions
                val rawAlcohol = d.strAlcoholic
                val cat = d.strCategory

                alcoholic = when {
                    cat?.contains("Soft", ignoreCase = true) == true -> "Non alcoholic"
                    rawAlcohol.isNullOrBlank() -> "Unknown"
                    rawAlcohol.equals("Non alcoholic", ignoreCase = true) -> "Non alcoholic"
                    rawAlcohol.equals("Alcoholic", ignoreCase = true) -> "Alcoholic"
                    rawAlcohol.equals("Optional alcohol", ignoreCase = true) -> "Optional alcohol"
                    else -> rawAlcohol
                }

                val ing = listOf(
                    d.strIngredient1, d.strIngredient2, d.strIngredient3, d.strIngredient4, d.strIngredient5,
                    d.strIngredient6, d.strIngredient7, d.strIngredient8, d.strIngredient9, d.strIngredient10,
                    d.strIngredient11, d.strIngredient12, d.strIngredient13, d.strIngredient14, d.strIngredient15
                )

                val meas = listOf(
                    d.strMeasure1, d.strMeasure2, d.strMeasure3, d.strMeasure4, d.strMeasure5,
                    d.strMeasure6, d.strMeasure7, d.strMeasure8, d.strMeasure9, d.strMeasure10,
                    d.strMeasure11, d.strMeasure12, d.strMeasure13, d.strMeasure14, d.strMeasure15
                )

                ingredients = ing.zip(meas).mapNotNull { (i, m) ->
                    val ii = i?.trim().orEmpty()
                    if (ii.isBlank()) null
                    else {
                        val mm = m?.trim().orEmpty()
                        if (mm.isBlank()) "• $ii" else "• $mm $ii"
                    }
                }
            } else {
                name = "Not found"
            }
        } catch (e: Exception) {
            name = "Error"
        }
    }

    Box(
        modifier = Modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally //centre tout
        ) {

            AsyncImage(
                model = thumb,
                contentDescription = name,
                modifier = Modifier
                    .size(200.dp)          // largeur = hauteur
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))// espaces en hauteur avant l'élément suivant

            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = category ?: "-",
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = alcoholic ?: "Unknown",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Glass:  ${glass ?: "-"}",
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface // fond différent
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "Ingredients",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ingredients.forEach { line ->
                        Text(
                            line,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp)) // espaces en hauteur avant l'élément suivant

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface // fond différent
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Instructions",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        instructions ?: "-",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}