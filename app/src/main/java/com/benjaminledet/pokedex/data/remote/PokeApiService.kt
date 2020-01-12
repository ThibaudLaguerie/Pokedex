package com.benjaminledet.pokedex.data.remote

import com.benjaminledet.pokedex.data.remote.response.*
import com.benjaminledet.pokedex.extensions.createDefaultService
import okhttp3.Interceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {

    @GET("pokemon")
    suspend fun getPokemonListAsync(@Query("offset") offset: Int): Response<PokeApiListResponse>

    @GET("pokemon/{id}")
    suspend fun getPokemonAsync(@Path("id") id: Int): Response<PokemonResponse>

    @GET("item/{name}")
    suspend fun getitemAsync(@Path("name") name: String): Response<ItemResponse>

    @GET("item-category/{name}")
    suspend fun getItemCategoryAsync(@Path("name") name: String): Response<ItemCategoryResponse>

    @GET("item-pocket")
    suspend fun getItemPocketListAsync(@Query("offset") offset: Int): Response<PokeApiListResponse>

    @GET("item-pocket/{name}")
    suspend fun getItemPocketAsync(@Path("name") name: String): Response<ItemPocketResponse>

    @GET("move/{name}")
    suspend fun getMoveAsync(@Path("name") name: String): Response<MoveResponse>

    companion object {
        private const val BASE_URL = "https://pokeapi.co/api/v2/"

        private const val LIMIT = "20"

        /**
         * Create the retrofit service
         * Add an interceptor to add the limit parameter to each query
         */
        fun create(): PokeApiService {

            val interceptor = object: Interceptor {
                override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                    var request = chain.request()
                    val url = request.url.newBuilder().addQueryParameter("limit", LIMIT).build()
                    request = request.newBuilder().url(url).build()
                    return chain.proceed(request)
                }
            }
            return Retrofit.Builder().createDefaultService(BASE_URL, interceptor)
        }
    }
}