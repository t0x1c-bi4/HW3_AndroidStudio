package com.example.hw3_androidstudio

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hw3_androidstudio.data.local.AppDatabase
import com.example.hw3_androidstudio.data.local.FakeApi
import com.example.hw3_androidstudio.data.model.Person
import com.example.hw3_androidstudio.data.repository.PeopleRepository
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryRoomTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: PeopleRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()

        repository = PeopleRepository(
            api = FakeApi(),
            dao = db.favouriteDao()
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    // тест: добавление и чтение из Room
    @Test
    fun insert_and_read_favourites() = runBlocking {

        val person = Person(1, "Luke", "", "", "", "", "", "", "")

        repository.addFavourite(person)

        val list = repository.getFavourites()

        Assert.assertEquals(1, list.size)
        Assert.assertEquals("Luke", list[0].name)
    }

    // тест: нет дублей
    @Test
    fun no_duplicates_in_room() = runBlocking {

        val person = Person(1, "Luke", "", "", "", "", "", "", "")

        repository.addFavourite(person)
        repository.addFavourite(person)

        val list = repository.getFavourites()

        Assert.assertEquals(1, list.size)
    }
}