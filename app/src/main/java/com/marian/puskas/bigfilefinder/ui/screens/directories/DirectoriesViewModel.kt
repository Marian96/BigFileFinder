package com.marian.puskas.bigfilefinder.ui.screens.directories

import android.os.Environment
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marian.puskas.bigfilefinder.domain.searchresults.directories.DirectoriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileFilter
import javax.inject.Inject

@HiltViewModel
class DirectoriesViewModel @Inject constructor(
    private val directoriesRepository: DirectoriesRepository
) : ViewModel() {

    private val _state: MutableState<DirectoriesState> = mutableStateOf(DirectoriesState.Loading)
    val state: State<DirectoriesState> = _state

    private var selectedDirectoriesPaths: List<String> = emptyList()
    private val rootPath = Environment.getExternalStorageDirectory().absolutePath
    private var currentPath: String = rootPath

    init {
        viewModelScope.launch {
            directoriesRepository.observeAllSelectedDirectories()
                .onStart { emit(directoriesRepository.getAllSelectedDirectories()) }
                .collectLatest {
                    selectedDirectoriesPaths = it
                    _state.value = getDirectories(currentPath)
                }
        }
    }

    private fun getDirectories(path: String): DirectoriesState {
        currentPath = path
        val directoriesNavigation = getDirectoriesNavigation(currentPath)
        val directoriesState = DirectoriesState.Directories(parentDirectories = directoriesNavigation)
        return File(path).listFiles(FileFilter { it.isDirectory })?.let { directories ->
            directoriesState.copy(data = directories.map { directory ->
                Directory(
                    name = directory.name,
                    path = directory.path,
                    isSelected = selectedDirectoriesPaths.contains(directory.path)
                )
            }.sortedBy { it.name })
        } ?: directoriesState
    }

    fun getSubDirectories(path: String) {
        _state.value = getDirectories(path)
    }

    fun onDirectorySelected(path: String, isSelected: Boolean) {
        if (isSelected) {
            directoriesRepository.saveDirectory(path)
        } else {
            directoriesRepository.removeDirectory(path)
        }
    }

    fun onRootDirectoryClick() {
        _state.value = getDirectories(rootPath)
    }

    private fun getDirectoriesNavigation(path: String): List<DirectoryNavigation> {
        return if (path == rootPath) {
            emptyList()
        } else {
            val parentDirectories = mutableListOf<DirectoryNavigation>()
            parentDirectories.add(DirectoryNavigation.RootDirectory)
            var directoryPath = rootPath
            val parentDirectoriesWithoutRoot = path.substring(startIndex = rootPath.length)
            parentDirectories.addAll(
                parentDirectoriesWithoutRoot
                    .split(SPLIT_DELIMITER)
                    .filter { it.isNotEmpty() }
                    .map {
                        directoryPath += SPLIT_DELIMITER + it
                        DirectoryNavigation.ParentDirectory(name = it, path = directoryPath, isClickable = directoryPath != path)
                    }
            )
            parentDirectories
        }
    }

    sealed class DirectoriesState {
        object Loading : DirectoriesState()
        data class Directories(
            val parentDirectories: List<DirectoryNavigation> = emptyList(),
            val data: List<Directory> = emptyList()
        ) : DirectoriesState()
    }

    data class Directory(
        val name: String,
        val path: String,
        val isSelected: Boolean
    )

    sealed class DirectoryNavigation(open val isClickable: Boolean) {
        object RootDirectory: DirectoryNavigation(isClickable = true)
        data class ParentDirectory(val name: String, val path: String, override val isClickable: Boolean): DirectoryNavigation(isClickable)
    }

    companion object {
        private const val SPLIT_DELIMITER = "/"
    }
}