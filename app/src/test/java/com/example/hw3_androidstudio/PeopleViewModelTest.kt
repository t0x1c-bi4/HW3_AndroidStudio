package com.example.hw3_androidstudio

import com.example.hw3_androidstudio.data.local.FakeApi
import com.example.hw3_androidstudio.data.local.FakeDao
import com.example.hw3_androidstudio.data.model.Person
import com.example.hw3_androidstudio.data.model.PersonDto
import com.example.hw3_androidstudio.data.repository.PeopleRepository
import com.example.hw3_androidstudio.viewmodel.PeopleViewModel
import com.example.hw3_androidstudio.viewmodel.PeopleUiState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Rule
import org.junit.Test

class TestRepository(
    api: FakeApi,
    dao: FakeDao
) : PeopleRepository(api, dao)

class PeopleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun testDto(): PersonDto {
        return PersonDto(
            name = "Luke",
            height = "",
            mass = "",
            hair_color = "",
            skin_color = "",
            eye_color = "",
            birth_year = "",
            gender = "",
            url = "https://swapi.dev/api/people/1/"
        )
    }

    // Юнит тесты

    // тест: успешная загрузка
    @Test
    fun successful_load() = runTest {
        val api = FakeApi()
        val dao = FakeDao()

        api.people = listOf(testDto())

        val vm = PeopleViewModel(TestRepository(api, dao))
        vm.load()

        advanceUntilIdle()

        assertTrue(vm.state is PeopleUiState.Success)
    }

    // тест: ошибка загрузки
    @Test
    fun error_load() = runTest {
        val api = FakeApi()
        val dao = FakeDao()

        api.shouldThrowError = true

        val vm = PeopleViewModel(TestRepository(api, dao))
        vm.load()

        advanceUntilIdle()

        assertTrue(vm.state is PeopleUiState.Error)
    }

    // тест: retry() после ошибки
    @Test
    fun retry_after_error() = runTest {
        val api = FakeApi()
        val dao = FakeDao()

        api.shouldThrowError = true

        val vm = PeopleViewModel(TestRepository(api, dao))
        vm.load()

        advanceUntilIdle()

        api.shouldThrowError = false
        api.people = listOf(testDto())

        vm.load()

        advanceUntilIdle()

        assertTrue(vm.state is PeopleUiState.Success)
    }

    // тест: начальное состояние
    @Test
    fun initial_state() {
        val vm = PeopleViewModel(TestRepository(FakeApi(), FakeDao()))
        assertTrue(vm.state is PeopleUiState.Loading)
    }

    // тест: обработка пустого результата
    @Test
    fun empty_result_handled_correctly() = runTest {
        val api = FakeApi()
        val dao = FakeDao()

        api.people = emptyList()

        val vm = PeopleViewModel(
            PeopleRepository(api, dao)
        )

        vm.load()

        advanceUntilIdle()

        assertTrue(vm.state is PeopleUiState.Empty)
    }

    // тест: обработка пустого результата
    @Test
    fun empty_result() = runTest {
        val api = FakeApi()
        val dao = FakeDao()

        api.people = emptyList()

        val vm = PeopleViewModel(TestRepository(api, dao))
        vm.load()

        advanceUntilIdle()

        assertTrue(vm.state is PeopleUiState.Empty)
    }

    // Нетривиальные тесты

    // тест: retry() инициирует новую попытку запроса
    @Test
    fun retry_triggers_new_request() = runTest {
        val api = FakeApi()
        val dao = FakeDao()

        var calls = 0

        val repo = object : PeopleRepository(api, dao) {
            override suspend fun getPeople(page: Int): Pair<List<Person>, Boolean> {
                calls++
                return super.getPeople(page)
            }
        }

        api.shouldThrowError = true

        val vm = PeopleViewModel(repo)
        vm.load()

        advanceUntilIdle()

        api.shouldThrowError = false
        api.people = listOf(testDto())

        vm.load()

        assertTrue(calls >= 2)
    }

    // тест: повторное добавление не создаёт дубль
    @Test
    fun no_duplicate_favourites() = runTest {
        val dao = FakeDao()
        val vm = PeopleViewModel(
            PeopleRepository(FakeApi(), dao)
        )

        val person = Person(1, "Luke", "", "", "", "", "", "", "")

        vm.toggleFav(person)
        vm.toggleFav(person)
        vm.toggleFav(person)

        advanceUntilIdle()

        val list = dao.getAll()

        assertEquals(1, list.size)
    }


    // тест: пустой результат дает empty, а не Success(emptyList)
    @Test
    fun empty_is_not_success() = runTest {
        val api = FakeApi()
        val dao = FakeDao()

        api.people = emptyList()

        val vm = PeopleViewModel(
            PeopleRepository(api, dao)
        )

        vm.load()

        advanceUntilIdle()

        assertFalse(vm.state is PeopleUiState.Success)
        assertTrue(vm.state is PeopleUiState.Empty)
    }
}