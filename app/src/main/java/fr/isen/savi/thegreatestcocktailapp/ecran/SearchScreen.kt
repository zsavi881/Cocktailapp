package fr.isen.savi.thegreatestcocktailapp.ecran

import androidx.navigation.NavController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import fr.isen.savi.thegreatestcocktailapp.network.NetworkCocktail
import fr.isen.savi.thegreatestcocktailapp.network.RetrofitInstance
import androidx.compose.foundation.lazy.items



@Composable
fun SearchScreen(navController: NavController) {

    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<NetworkCocktail>>(emptyList()) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search cocktail") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val res = RetrofitInstance.api.searchCocktail(query)
                        results = res.drinks ?: emptyList()
                    } catch (e: Exception) {
                        results = emptyList()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(results) { drink ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            navController.navigate("detail/${drink.idDrink}")
                        }
                ) {
                    Text(
                        text = drink.strDrink ?: "-",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
