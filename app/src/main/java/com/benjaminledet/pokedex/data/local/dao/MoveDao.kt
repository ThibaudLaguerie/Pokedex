package com.benjaminledet.pokedex.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.benjaminledet.pokedex.data.model.Move

@Dao
abstract class MoveDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(data: List<Move>): List<Long>

    @Delete
    abstract suspend fun delete(data: List<Move>)

    @Query("DELETE FROM ${Move.TABLE_NAME}")
    abstract suspend fun deleteAll()

    @Query("SELECT * FROM ${Move.TABLE_NAME} WHERE ${Move.ID} = :id")
    abstract suspend fun getById(id: Int?): Move?

    @Query("SELECT * FROM ${Move.TABLE_NAME} WHERE ${Move.ID} = :id")
    abstract fun getByIdObservable(id: Int?): LiveData<Move?>

    @Query("SELECT * FROM ${Move.TABLE_NAME}")
    abstract suspend fun getAll(): List<Move>

    @Query("SELECT * FROM ${Move.TABLE_NAME}")
    abstract fun getAllObservable(): LiveData<List<Move>>

    @Query("SELECT * FROM ${Move.TABLE_NAME} WHERE ${Move.NAME} in(:names)")
    abstract fun getAllObservable(names: List<String>): LiveData<List<Move>>

    @Query("SELECT * FROM ${Move.TABLE_NAME}")
    abstract fun getAllPaged(): DataSource.Factory<Int, Move>
}