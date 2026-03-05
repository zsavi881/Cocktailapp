package fr.isen.savi.thegreatestcocktailapp.network

import com.google.gson.annotations.SerializedName

data class CocktailResponse(
    val drinks: List<NetworkCocktail>?
)

data class NetworkCocktail(
    val idDrink: String? = null,
    val strDrink: String? = null,
    val strCategory: String? = null,
    val strGlass: String? = null,
    val strInstructions: String? = null,
    val strDrinkThumb: String? = null,

    val strIngredient1: String? = null,
    val strIngredient2: String? = null,
    val strIngredient3: String? = null,
    val strIngredient4: String? = null,
    val strIngredient5: String? = null,
    val strIngredient6: String? = null,
    val strIngredient7: String? = null,
    val strIngredient8: String? = null,
    val strIngredient9: String? = null,
    val strIngredient10: String? = null,
    val strIngredient11: String? = null,
    val strIngredient12: String? = null,
    val strIngredient13: String? = null,
    val strIngredient14: String? = null,
    val strIngredient15: String? = null,

    val strMeasure1: String? = null,
    val strMeasure2: String? = null,
    val strMeasure3: String? = null,
    val strMeasure4: String? = null,
    val strMeasure5: String? = null,
    val strMeasure6: String? = null,
    val strMeasure7: String? = null,
    val strMeasure8: String? = null,
    val strMeasure9: String? = null,
    val strMeasure10: String? = null,
    val strMeasure11: String? = null,
    val strMeasure12: String? = null,
    val strMeasure13: String? = null,
    val strMeasure14: String? = null,
    val strMeasure15: String? = null,

    @SerializedName("strAlcoholic")
    val strAlcoholic: String?
)

data class DrinksByCategoryResponse(
    val drinks: List<DrinkLiteDto>
)

data class DrinkLiteDto(
    val strDrink: String,
    val strDrinkThumb: String,
    val idDrink: String
)
data class CategoryListResponse(
    val drinks: List<CategoryDto>
)

data class CategoryDto(
    val strCategory: String
)

