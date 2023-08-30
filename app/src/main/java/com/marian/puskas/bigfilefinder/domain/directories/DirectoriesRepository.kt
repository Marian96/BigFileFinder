package com.marian.puskas.bigfilefinder.domain.searchresults.directories

import kotlinx.coroutines.flow.Flow

interface DirectoriesRepository {
    fun getAllSelectedDirectories(): List<String>
    fun observeAllSelectedDirectories(): Flow<List<String>>
    fun saveDirectory(path: String)
    fun removeDirectory(path: String)
}