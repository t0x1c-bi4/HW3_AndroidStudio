package com.example.hw3_androidstudio.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites")
data class FavouriteEntity(

    @PrimaryKey
    val id: Int,

    val name: String
)