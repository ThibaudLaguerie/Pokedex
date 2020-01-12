package com.benjaminledet.pokedex.data

import androidx.room.Room
import com.benjaminledet.pokedex.data.local.PokedexDatabase
import com.benjaminledet.pokedex.data.remote.PokeApiClient
import com.benjaminledet.pokedex.data.remote.PokeApiService
import com.benjaminledet.pokedex.data.repository.ItemRepository
import com.benjaminledet.pokedex.data.repository.PokemonRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module

val dataModule = module {

    single { ItemRepository() }

    single { PokemonRepository() }

    //region local

    single { Room.databaseBuilder(androidApplication(), PokedexDatabase::class.java, PokedexDatabase.DATABASE_NAME).build() }

    single { get<PokedexDatabase>().itemDao() }

    single { get<PokedexDatabase>().itemCategoryDao() }

    single { get<PokedexDatabase>().itemPocketDao() }

    single { get<PokedexDatabase>().pokemonDao() }

    single { get<PokedexDatabase>().moveDao() }

    //endregion

    //region remote

    single { PokeApiClient() }

    single { PokeApiService.create() }

    //endregion

}
