package com.benjaminledet.pokedex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.benjaminledet.pokedex.data.local.converter.StringListConverter
import com.benjaminledet.pokedex.data.local.dao.*
import com.benjaminledet.pokedex.data.model.*

@Database(
    entities = [
        Pokemon::class, Item::class, ItemPocket::class, ItemCategory::class, Move::class
    ],
    version = PokedexDatabase.VERSION, exportSchema = true
)
@TypeConverters(StringListConverter::class)
abstract class PokedexDatabase: RoomDatabase() {

    abstract fun itemDao(): ItemDao

    abstract fun itemCategoryDao(): ItemCategoryDao

    abstract fun itemPocketDao(): ItemPocketDao

    abstract fun pokemonDao(): PokemonDao

    abstract fun moveDao(): MoveDao

    companion object {
        const val DATABASE_NAME = "PokedexDatabase"
        const val VERSION = 1
    }
}