package com.marian.puskas.bigfilefinder.ui.screens.searchresults

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marian.puskas.bigfilefinder.R
import com.marian.puskas.bigfilefinder.common.notifications.NotificationProvider
import com.marian.puskas.bigfilefinder.common.notifications.model.NotificationMessage
import com.marian.puskas.bigfilefinder.common.resources.ResourcesManager
import com.marian.puskas.bigfilefinder.domain.searchresults.BigFilesSearchManager
import com.marian.puskas.bigfilefinder.domain.searchresults.SearchResult
import com.marian.puskas.bigfilefinder.ui.Parameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SearchResultsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bigFileSearchManager: BigFilesSearchManager,
    private val notificationProvider: NotificationProvider,
    private val resourcesManager: ResourcesManager
) : ViewModel() {

    private val _state: MutableState<FilesState> = mutableStateOf(FilesState.Loading(""))
    val state: State<FilesState> = _state

    init {
        viewModelScope.launch {
            bigFileSearchManager.observeSearchResult().collectLatest { searchResult ->
                when (searchResult) {
                    is SearchResult.Searching -> _state.value = FilesState.Loading(searchResult.searchedDirectory)
                    is SearchResult.SearchResults -> {
                        _state.value = FilesState.Data(searchResult.data.map { file ->
                            file.toBigFile()
                        })
                        sendNotification(searchResult.data.count())
                    }
                }
            }
        }
        viewModelScope.launch {
            savedStateHandle.get<String>(Parameters.PARAM_NUMBER_OF_FILES)?.let {
                val numberOfFiles = it.toInt()
                bigFileSearchManager.executeSearch(numberOfFiles)
            }
        }
    }

    private fun sendNotification(searchResultsNumber: Int) {
        notificationProvider.sendNotification(
            NotificationMessage(
                title = resourcesManager.getString(R.string.search_notification_title),
                description = resourcesManager.getString(R.string.search_notification_description, searchResultsNumber)
            )
        )
    }

    private fun File.toBigFile(): BigFile =
        BigFile(
            fileName = name,
            fileSize = length().getFileSize(),
            path = path
        )

    private fun Long.getFileSize(): String {
        val fileSizeInKB = this / 1024
        val fileSizeInMB = fileSizeInKB / 1024
        if (fileSizeInMB != 0L) return "$fileSizeInMB MB"
        if (fileSizeInKB != 0L) return "$fileSizeInKB KB"
        return "$this bytes"
    }

    sealed class FilesState {
        data class Loading(val searchedDirectory: String) : FilesState()
        data class Data(val files: List<BigFile>) : FilesState()
    }

    data class BigFile(
        val fileSize: String,
        val fileName: String,
        val path: String
    )
}