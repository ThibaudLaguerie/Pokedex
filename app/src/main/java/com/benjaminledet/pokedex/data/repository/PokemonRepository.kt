package com.benjaminledet.pokedex.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.benjaminledet.pokedex.data.local.dao.MoveDao
import com.benjaminledet.pokedex.data.local.dao.PokemonDao
import com.benjaminledet.pokedex.data.model.Move
import com.benjaminledet.pokedex.data.model.Pokemon
import com.benjaminledet.pokedex.data.remote.PokeApiClient
import com.benjaminledet.pokedex.data.repository.utils.BoundaryCallback
import com.benjaminledet.pokedex.data.repository.utils.Listing
import com.benjaminledet.pokedex.data.repository.utils.NetworkState
import com.benjaminledet.pokedex.data.repository.utils.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class PokemonRepository: KoinComponent {

    private val pokemonDao by inject<PokemonDao>()
    private val moveDao by inject<MoveDao>()

    private val pokeApiClient by inject<PokeApiClient>()

    fun getMovesObservable(names: List<String>) = moveDao.getAllObservable(names)

    fun getPokemonObservable(id: Int) = pokemonDao.getByIdObservable(id)

    fun getAllPokemonsObservable() = pokemonDao.getAllObservable()

    fun getAllPokemonsPagedList(scope: CoroutineScope, pageSize: Int): Listing<Pokemon> {

        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = BoundaryCallback<Pokemon>(
            scope = scope,
            loadMore = { offset ->
                val response = pokeApiClient.getPokemonList(offset)
                insertPokemons(response)
            },
            getOffset = { pokemon -> pokemon.id }
        )

        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refreshPokemons(scope)
        }

        val pagedList = pokemonDao.getAllPaged().toLiveData(
            pageSize = pageSize,
            boundaryCallback = boundaryCallback
        )

        return Listing(
            pagedList = pagedList,
            refresh = {
                refreshTrigger.value = null
            },
            networkState = boundaryCallback.networkState,
            refreshState = refreshState
        )
    }

    /**
     * Refresh a pokemon with its ID
     * Same process as [refreshPokemons]
     */
    fun refreshPokemon(scope: CoroutineScope, id: Int): LiveData<NetworkState> {
        val networkState = MutableLiveData(NetworkState.LOADING)
        Log.v(TAG, "refresh pokemon: ${Status.RUNNING}")
        scope.launch {
            try {
                val pokemon = pokeApiClient.getPokemonDetail(id)
                insertPokemons(listOf(pokemon))
                pokemon.detail?.moves?.let { list ->
                    if (list.isNotEmpty()) {
                        val moves = pokeApiClient.getMoves(list)
                        insertMoves(moves)
                    }
                }
                networkState.postValue(NetworkState.LOADED)
                Log.v(TAG, "refresh pokemon: ${Status.SUCCESS}")

            } catch (e: Exception) {
                networkState.postValue(NetworkState.error(e.message))
                Log.e(TAG, "refresh pokemon: ${Status.FAILED} : ${e.message}", e)
            }
        }
        return networkState
    }

    /**
     * Refresh all the pokemons
     * When refresh is called, we simply run a fresh network request and when it arrives, clear
     * the database table and insert all new items in a transaction.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     */
    private fun refreshPokemons(scope: CoroutineScope): LiveData<NetworkState> {
        val networkState = MutableLiveData(NetworkState.LOADING)
        Log.v(TAG, "refresh pokemons: ${Status.RUNNING}")

        scope.launch {
            try {
                val pokemons = pokeApiClient.getPokemonList(0)
                pokemonDao.deleteAll()
                insertPokemons(pokemons)
                networkState.postValue(NetworkState.LOADED)
                Log.v(TAG, "refresh pokemons: ${Status.SUCCESS}")

            } catch (e: Exception) {
                networkState.postValue(NetworkState.error(e.message))
                Log.e(TAG, "refresh pokemons: ${Status.FAILED} : ${e.message}", e)
            }
        }
        return networkState
    }

    private suspend fun insertPokemons(pokemons: List<Pokemon>) {
        Log.v(TAG, "insert pokemons: $pokemons")
        pokemonDao.insert(pokemons)
    }

    private suspend fun insertMoves(moves: List<Move>) {
        Log.v(TAG, "insert moves: $moves")
        moveDao.insert(moves)
    }

    companion object {
        private const val TAG = "PokemonRepository"
    }
}