package com.example.hw3_androidstudio.data.local

class FakeDao : FavouriteDao {

    private val list = mutableListOf<FavouriteEntity>()

    override suspend fun getAll(): List<FavouriteEntity> = list

    override suspend fun insert(entity: FavouriteEntity) {
        if (list.none { it.id == entity.id }) {
            list.add(entity)
        }
    }

    override suspend fun delete(entity: FavouriteEntity) {
        list.removeIf { it.id == entity.id }
    }
}