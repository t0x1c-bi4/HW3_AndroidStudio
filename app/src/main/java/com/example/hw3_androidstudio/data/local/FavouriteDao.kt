package com.example.hw3_androidstudio.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {

    @Query("SELECT * FROM favourites")
    suspend fun getAll(): List<FavouriteEntity>

    @Query("SELECT * FROM favourites")
    fun observeAll(): Flow<List<FavouriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavouriteEntity)

    @Delete
    suspend fun delete(entity: FavouriteEntity)
}