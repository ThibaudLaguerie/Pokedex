package com.benjaminledet.pokedex.data.remote

import com.benjaminledet.pokedex.data.model.*
import com.benjaminledet.pokedex.data.remote.response.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import retrofit2.Response

class PokeApiClient: KoinComponent {

    private val service by inject<PokeApiService>()

    /**
     * Get a list of pokemon from the offset
     */
    suspend fun getPokemonList(offset: Int): List<Pokemon> {
        val response = performRequest {
            service.getPokemonListAsync(offset)
        }
        return response.results.map { apiResourceResponseToPokemon(it) }
    }

    suspend fun getMoves(list: List<String>): List<Move> {
        return coroutineScope {
            list.parallelMap(this) { name ->
                val response = performRequest {
                    service.getMoveAsync(name)
                }
                moveResponseToMove(response)
            }
        }.toList()
    }

    /**
     * Get a pokemon by its id
     */
    suspend fun getPokemonDetail(id: Int): Pokemon {
        val response = performRequest {
            service.getPokemonAsync(id)
        }
        return pokemonResponseToPokemon(response)
    }
    /**
     * Get a list of itemPocket from the offset
     * Then get the detail of each itemPocket
     */
    suspend fun getItemPocketList(offset: Int): List<ItemPocket> {
        val response = performRequest {
            service.getItemPocketListAsync(offset)
        }
        return coroutineScope {
            response.results
                .mapNotNull { apiResourceResponse -> apiResourceResponse.name }
                .parallelMap(this) { name ->
                    performRequest {
                        service.getItemPocketAsync(name)
                    }
                }
                .map { itemPocketResponse -> itemPocketResponseToItemPocket(itemPocketResponse) }
        }
    }

    /**
     * Get the detail of each itemCategory associated to their items from a list of ids
     */
    suspend fun getItemCategoryListWithItems(idList: List<String>, itemPocketId: Int): Map<ItemCategory, List<Item>> {
        return coroutineScope {
            idList
                .parallelMap(this) { id ->
                    performRequest {
                        service.getItemCategoryAsync(id)
                    }
                }
                .map { itemCategoryResponse ->
                    val itemCategory = itemCategoryResponseToItemCategory(itemCategoryResponse, itemPocketId)
                    itemCategory to itemCategoryResponse.items.map { apiResourceResponseToItem(it, itemCategory.id) }
                }.toMap()
        }
    }

    private fun apiResourceResponseToPokemon(apiResourceResponse: ApiResourceResponse) = Pokemon(
        id = getPokemonIdFromUrl(apiResourceResponse.url) ?: 0,
        name = apiResourceResponse.name?.capitalize() ?: "",
        iconUrl = getPokemonBeautifulIconUrl(getPokemonIdFromUrl(apiResourceResponse.url)), //getPokemonIconUrl(getPokemonIdFromUrl(apiResourceResponse.url)),
        detail = null
    )

    private fun apiResourceResponseToItem(apiResourceResponse: ApiResourceResponse, itemCategoryId: Int) = Item(
        id = getItemIdFromUrl(apiResourceResponse.url) ?: 0,
        name = apiResourceResponse.name?.capitalize() ?: "",
        iconUrl = getItemIconUrl(apiResourceResponse.name),
        itemCategoryId = itemCategoryId
    )

    private fun pokemonResponseToPokemon(pokemonResponse: PokemonResponse) = Pokemon(
        id = pokemonResponse.id,
        name = pokemonResponse.name.capitalize(),
        iconUrl = getPokemonBeautifulIconUrl(pokemonResponse.id),//pokemonResponse.sprites[PokemonResponse.DEFAULT_SPRITE],
        detail = PokemonDetail(
            weight = pokemonResponse.weight / 10,
            height = pokemonResponse.height / 10,
            types = pokemonResponse.types.mapNotNull { it.type.name },
            moves = pokemonResponse.moves.mapNotNull { it.move.name}
        )
    )

    private fun moveResponseToMove(moveResponse: MoveResponse) = Move(
        id = moveResponse.id,
        name = moveResponse.name,
        type = moveResponse.type.name ?:"",
        accuracy = moveResponse.accuracy,
        power = moveResponse.power,
        pp = moveResponse.pp
    )

    private fun itemResponseToItem(itemResponse: ItemResponse, itemCategoryId: Int) = Item(
        id = itemResponse.id,
        name = itemResponse.name.capitalize(),
        iconUrl = itemResponse.sprites[ItemResponse.DEFAULT_SPRITE],
        itemCategoryId = itemCategoryId
    )

    private fun itemCategoryResponseToItemCategory(itemCategoryResponse: ItemCategoryResponse, itemPocketId: Int) = ItemCategory(
        id = itemCategoryResponse.id,
        name = itemCategoryResponse.name.capitalize(),
        itemPocketId = itemPocketId,
        itemNameList = itemCategoryResponse.items.mapNotNull { it.name }
    )

    private fun itemPocketResponseToItemPocket(itemPocketResponse: ItemPocketResponse) = ItemPocket(
        id = itemPocketResponse.id,
        name = itemPocketResponse.name.capitalize(),
        itemCategoryNameList = itemPocketResponse.categories.mapNotNull { it.name }
    )

    /**
     * Perform an async request and spread errors
     * Usually we would create our own errors from the api here
     */
    private suspend fun <T>performRequest(block: suspend CoroutineScope.() -> Response<T>): T {
        val response = try {
            coroutineScope(block)
        } catch (exception: Exception) {
            throw exception
        }

        if (!response.isSuccessful) throw Exception(response.errorBody()?.string())

        return response.body() ?: throw Exception("body is null")
    }

    /**
     * Allow doing simultaneous requests.
     * A normal map will wait for the response of the current block to start the next
     * This one doesn't wait and start all the blocks simultaneously
     * Much faster!
     */
    private suspend fun <A, B> Collection<A>.parallelMap(scope: CoroutineScope, block: suspend (A) -> B): Collection<B> {
        return map {
            scope.async {
                block(it)
            }
        }.map {
            it.await()
        }
    }

    private fun getPokemonIdFromUrl(url: String?): Int? = url?.substringAfter("pokemon/")?.substringBefore("/")?.toIntOrNull()

    private fun getItemIdFromUrl(url: String?): Int? = url?.substringAfter("item/")?.substringBefore("/")?.toIntOrNull()

    private fun getPokemonIconUrl(id: Int?) = "$POKEMON_ICON_BASE_URL$id$ICON_EXTENSION"

    private fun getPokemonBeautifulIconUrl(id: Int?) = "$POKEMON_BEAUTIFUL_ICON_BASE_URL${String.format("%03d", id)}$ICON_EXTENSION"

    private fun getItemIconUrl(name: String?) = "$ITEM_ICON_BASE_URL$name$ICON_EXTENSION"

    companion object {
        private const val TAG = "PokeApiClient"
        private const val ICON_EXTENSION = ".png"
        private const val POKEMON_ICON_BASE_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"
        private const val POKEMON_BEAUTIFUL_ICON_BASE_URL = "https://assets.pokemon.com/assets/cms2/img/pokedex/full/"
        private const val ITEM_ICON_BASE_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/"
    }
}