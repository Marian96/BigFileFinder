package com.marian.puskas.bigfilefinder.ui.screens.searchresults

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.marian.puskas.bigfilefinder.R
import com.marian.puskas.bigfilefinder.ui.composables.TopBar
import com.marian.puskas.bigfilefinder.ui.screens.searchresults.SearchResultsViewModel.FilesState.Data
import com.marian.puskas.bigfilefinder.ui.screens.searchresults.SearchResultsViewModel.FilesState.Loading
import com.marian.puskas.bigfilefinder.ui.screens.searchresults.composables.FileListItem

@Composable
fun SearchResultsScreen(
    searchResultsViewModel: SearchResultsViewModel = hiltViewModel(),
    onBackButtonClick: () -> Unit
) {
    val state = searchResultsViewModel.state.value
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = stringResource(id = R.string.files),
            onNavigationClick = onBackButtonClick
        )
        when (state) {
            is Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = stringResource(id = R.string.searching_in, state.searchedDirectory), textAlign = TextAlign.Center)
                    }
                }
            }
            is Data -> {
                if (state.files.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = stringResource(id = R.string.no_files_found)
                        )
                    }
                } else {
                    LazyColumn {
                        state.files.forEach {
                            item { FileListItem(name = it.fileName, fileSize = it.fileSize, path = it.path) }
                        }
                    }
                }
            }
        }
    }
}