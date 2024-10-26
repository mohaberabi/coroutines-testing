package com.mohaberabi.coroutinetesting.repository

import com.mohaberabi.coroutinetesting.source.ColdDataSource
import com.mohaberabi.coroutinetesting.source.DataSource
import com.mohaberabi.coroutinetesting.source.HotDataSource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class UserRepositoryImplTest {


    private lateinit var userRepositoryImpl: UserRepositoryImpl


    private lateinit var dataSource: DataSource

    @Before
    fun setup() {
        dataSource = ColdDataSource()
        userRepositoryImpl = UserRepositoryImpl(dataSource)
    }

    @Test
    fun `test hello world`() = runTest {

        val result = userRepositoryImpl.helloWorld()
        assertEquals(result, "hello world")
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `initialize the database passes`() = runTest {

        userRepositoryImpl.databaseInitialization()
        userRepositoryImpl.insertDatabase(listOf("a"))//now
        userRepositoryImpl.insertDatabase(listOf("b"))//now
        userRepositoryImpl.insertDatabase(listOf("c"))//now
        userRepositoryImpl.insertDatabase(listOf("d"))//now

        userRepositoryImpl.insertDatabase(listOf("e", "f", "g", "h")) // now * 4
        advanceUntilIdle()
        val expected = listOf("a", "b", "c", "d", "e", "f", "g", "h")
        val actual = userRepositoryImpl.fetchUsers().sorted()
        assertEquals(expected, actual)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `initialize the database passes when switching the context injected`() = runTest {

        val repo = UserRepositoryImpl(dataSource, StandardTestDispatcher(testScheduler))
        repo.databaseInitialization()
        repo.insertDatabase(listOf("a"))//now
        repo.insertDatabase(listOf("b"))//now
        repo.insertDatabase(listOf("c"))//now
        repo.insertDatabase(listOf("d"))//now

        repo.insertDatabase(listOf("e", "f", "g", "h")) // now * 4
        advanceUntilIdle()
        val expected = listOf("a", "b", "c", "d", "e", "f", "g", "h")
        val actual = repo.fetchUsers().sorted()
        assertEquals(expected, actual)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `initialize the database passes eagerly`() = runTest(UnconfinedTestDispatcher()) {

        userRepositoryImpl.databaseInitialization()
        userRepositoryImpl.insertDatabase(listOf("a"))//now
        userRepositoryImpl.insertDatabase(listOf("b"))//now
        userRepositoryImpl.insertDatabase(listOf("c"))//now
        userRepositoryImpl.insertDatabase(listOf("d"))//now

        userRepositoryImpl.insertDatabase(listOf("e", "f", "g", "h")) // now * 4
        val expected = listOf("a", "b", "c", "d", "e", "f", "g", "h")
        val actual = userRepositoryImpl.fetchUsers().sorted()
        assertEquals(expected, actual)
    }


    @Test
    fun `initialize the database never passes`() = runTest {

        userRepositoryImpl.databaseInitialization()
        userRepositoryImpl.insertDatabase(listOf("a"))//now
        userRepositoryImpl.insertDatabase(listOf("b"))//now
        userRepositoryImpl.insertDatabase(listOf("c"))//now
        userRepositoryImpl.insertDatabase(listOf("d"))//now

        userRepositoryImpl.insertDatabase(listOf("e", "f", "g", "h")) // now * 4
        val expected = listOf("a", "b", "c", "d", "e", "f", "g", "h")
        val actual = userRepositoryImpl.fetchUsers().sorted()
        assertEquals(expected, actual)
    }

    @Test
    fun ` should return the correct values collection when cold flow `() = runTest {
        val list = userRepositoryImpl.getScores().toList()
        val expected = listOf(1, 2, 3, 4, 5, 6)
        assertEquals(list, expected)
    }

    @Test
    fun ` should return the correct values collection when hot flow  lasts forever without any result `() =
        runTest {
            val repo = UserRepositoryImpl(HotDataSource())

            val result = mutableListOf<Int>()
            repo.getScores().collect {
                result.add(it)
            }
            repo.emit(1)
            assertEquals(result[0], 1)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun ` should return the correct values collection when hot flow passes when manuall cacelation `() =
        runTest {
            val repo = UserRepositoryImpl(HotDataSource())
            val result = mutableListOf<Int>()
            val job = launch(UnconfinedTestDispatcher()) {
                repo.getScores().collect {
                    result.add(it)
                }
            }
            repo.emit(1)
            assertEquals(result[0], 1)
            job.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun ` should return the correct values collection when hot flow passes when using background scope  `() =
        runTest {
            val repo = UserRepositoryImpl(HotDataSource())
            val result = mutableListOf<Int>()
            backgroundScope.launch(UnconfinedTestDispatcher()) {
                repo.getScores().collect {
                    result.add(it)
                }
            }
            repo.emit(1)
            assertEquals(result[0], 1)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun ` should return the correct values collection when hot flow passes when using background scope 2 `() =
        runTest {
            val repo = UserRepositoryImpl(HotDataSource())
            val result = mutableListOf<Int>()
            backgroundScope.launch(UnconfinedTestDispatcher()) {
                repo.getScores().collect {
                    result.add(it)
                }
            }
            repo.emit(1)
            repo.emit(2)
            repo.emit(3)
            repo.emit(4)
            assertEquals(result[0], 1)
            assertEquals(result[1], 2)
            assertEquals(result[2], 3)
            assertEquals(result[3], 4)

        }
}