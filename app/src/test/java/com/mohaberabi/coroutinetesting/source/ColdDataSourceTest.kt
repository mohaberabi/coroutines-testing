package com.mohaberabi.coroutinetesting.source

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class ColdDataSourceTest {


    private lateinit var coldSource: ColdDataSource

    @Before
    fun setup() {
        coldSource = ColdDataSource()
    }


    @Test
    fun ` should return the correct values collection`() = runTest {
        val list = coldSource.counts().toList()
        val expected = listOf(1, 2, 3, 4, 5, 6)
        assertEquals(list, expected)
    }
}