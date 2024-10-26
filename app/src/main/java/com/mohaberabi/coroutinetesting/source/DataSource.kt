package com.mohaberabi.coroutinetesting.source

import kotlinx.coroutines.flow.Flow

interface DataSource {

    suspend fun emit(value: Int)

    suspend fun counts(): Flow<Int>


}