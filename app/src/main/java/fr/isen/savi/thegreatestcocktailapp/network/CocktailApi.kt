package fr.isen.savi.thegreatestcocktailapp.network

import retrofit2.http.GET
import retrofit2.http.Query


interface CocktailApi {

    @GET("search.php")
    suspend fun searchCocktail(
        @Query("s") name: String
    ): CocktailResponse

    @GET("lookup.php")
    suspend fun cocktailById(
        @Query("i") id: String
    ): CocktailResponse

     @GET("filter.php")
    suspend fun getDrinksByCategory(@Query("c") category: String): DrinksByCategoryResponse
    @GET("list.php?c=list")
    suspend fun getCategories(): CategoryListResponse

    @GET("random.php")
    suspend fun getRandomCocktail(): CocktailResponse




}


