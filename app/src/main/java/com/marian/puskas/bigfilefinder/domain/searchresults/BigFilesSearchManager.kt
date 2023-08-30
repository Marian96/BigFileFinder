package com.marian.puskas.bigfilefinder.domain.searchresults

import android.os.Environment
import com.marian.puskas.bigfilefinder.domain.searchresults.directories.DirectoriesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

private const val TAG = "BigFilesSearchManager"

class BigFilesSearchManager @Inject constructor(
    directoriesRepository: DirectoriesRepository
) {
    private val allSelectedDirectories = directoriesRepository.getAllSelectedDirectories()
    private val allFiles = mutableListOf<File>()

    private val mutex = Mutex()
    private val searchResultFlow = MutableSharedFlow<SearchResult>()

    suspend fun executeSearch(numberOfFiles: Int) = withContext(Dispatchers.IO) {
        val externalStoragePath = Environment.getExternalStorageDirectory().path
        searchForFiles(externalStoragePath)
        val sortedFiles = allFiles.sortedByDescending { it.length() }
        searchResultFlow.emit(SearchResult.SearchResults(sortedFiles.take(numberOfFiles)))
    }

    fun observeSearchResult(): Flow<SearchResult> = searchResultFlow

    private suspend fun searchForFiles(directoryPath: String) {
        searchResultFlow.emit(SearchResult.Searching(directoryPath))
        try {
            File(directoryPath).listFiles()?.forEach { file ->
                if (file.isDirectory && allSelectedDirectories.contains(file.path)) {
                        searchForFiles(file.path)
                } else if (file.isFile) {
                    mutex.withLock { allFiles.add(file) }
                }
            }
        } catch (e: SecurityException) {
            Timber.i(TAG, e.message)
        }
    }
}

sealed class SearchResult {
    data class Searching(val searchedDirectory: String): SearchResult()
    data class SearchResults(val data: List<File>): SearchResult()
}