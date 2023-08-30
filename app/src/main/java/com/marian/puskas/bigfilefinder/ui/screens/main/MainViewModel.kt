package com.marian.puskas.bigfilefinder.ui.screens.main

import androidx.lifecycle.ViewModel
import com.marian.puskas.bigfilefinder.domain.searchresults.directories.DirectoriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val directoriesRepository: DirectoriesRepository,
) : ViewModel() {
    fun hasSelectedDirectories(): Boolean = directoriesRepository.getAllSelectedDirectories().isNotEmpty()
}