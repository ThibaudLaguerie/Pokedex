package com.benjaminledet.pokedex.ui.pokemon.detail

import android.app.Application
import androidx.lifecycle.*
import com.benjaminledet.pokedex.data.repository.PokemonRepository
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class PokemonDetailViewModel(application: Application): AndroidViewModel(application), KoinComponent {

    private val pokemonRepository by inject<PokemonRepository>()

    val pokemonId = MutableLiveData<Int>()

    val pokemon = pokemonId.switchMap { id -> pokemonRepository.getPokemonObservable(id) }

    val moves = pokemon.switchMap { pokemon ->
        pokemonRepository.getMovesObservable(pokemon?.detail?.moves ?: listOf())
    }

    val refreshState = pokemonId.switchMap { id -> pokemonRepository.refreshPokemon(viewModelScope, id) }

}