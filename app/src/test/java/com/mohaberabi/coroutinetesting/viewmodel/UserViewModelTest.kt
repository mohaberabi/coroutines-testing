package com.mohaberabi.coroutinetesting.viewmodel

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description


class UserViewModelTest {


    @get:Rule
    val rule = MainDispatcherRule()

    private
    lateinit var userViewModel: UserViewModel


    @Before
    fun setup() {
        userViewModel = UserViewModel()
    }


    @Test

    fun ` get user test never passed no looper for main thread`() = runTest {
        userViewModel.getUser()
        assertEquals(listOf("a", "b"), userViewModel.list.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test

    fun ` get user test passed when setting main disaptcher `() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        userViewModel.getUser()
        assertEquals(
            listOf("a", "b"),
            userViewModel.list.value,
        )
        Dispatchers.resetMain()

    }
}


class MainDispatcherRule(
    private val dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun starting(description: Description?) {
        Dispatchers.setMain(dispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}