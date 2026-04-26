package com.example.hw3_androidstudio.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeDao : FavouriteDao {

    private val list = mutableListOf<FavouriteEntity>()
    private val flow = MutableStateFlow<List<FavouriteEntity>>(emptyList())

    override suspend fun getAll(): List<FavouriteEntity> = list.toList()

    override fun observeAll(): Flow<List<FavouriteEntity>> = flow

    override suspend fun insert(entity: FavouriteEntity) {
        if (list.none { it.id == entity.id }) {
            list.add(entity)
            flow.value = list.toList()
        }
    }

    override suspend fun delete(entity: FavouriteEntity) {
        list.removeIf { it.id == entity.id }
        flow.value = list.toList()
    }
}