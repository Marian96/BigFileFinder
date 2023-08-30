package com.marian.puskas.bigfilefinder.ui.screens.directories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.marian.puskas.bigfilefinder.R
import com.marian.puskas.bigfilefinder.ui.composables.TopBar
import com.marian.puskas.bigfilefinder.ui.screens.directories.DirectoriesViewModel.DirectoriesState.*
import com.marian.puskas.bigfilefinder.ui.screens.directories.DirectoriesViewModel.DirectoryNavigation.ParentDirectory
import com.marian.puskas.bigfilefinder.ui.screens.directories.DirectoriesViewModel.DirectoryNavigation.RootDirectory
import com.marian.puskas.bigfilefinder.ui.screens.directories.composables.DirectoryListItem
import com.marian.puskas.bigfilefinder.ui.screens.directories.composables.ParentDirectory

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DirectoriesScreen(
    directoriesViewModel: DirectoriesViewModel = hiltViewModel(),
    navigateToMainScreen: () -> Unit
) {
    val state = directoriesViewModel.state.value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        TopBar(
            title = stringResource(id = R.string.directories),
            onNavigationClick = navigateToMainScreen
        )
        when (state) {
            is Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            is Directories -> {
                if (state.parentDirectories.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp), verticalArrangement = Arrangement.Center
                    ) {
                        state.parentDirectories.forEach { parentDir ->
                            ParentDirectory(
                                isRootDirectory = parentDir is RootDirectory,
                                isClickable = parentDir.isClickable,
                                parentDirectoryName = if (parentDir is ParentDirectory) parentDir.name else "",
                                onParentDirectoryClick = {
                                    when (parentDir) {
                                        is RootDirectory -> directoriesViewModel.onRootDirectoryClick()
                                        is ParentDirectory -> directoriesViewModel.getSubDirectories(
                                            parentDir.path
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
                if (state.data.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = stringResource(id = R.string.no_more_directories),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.Gray
                        )
                    }

                } else {
                    LazyColumn {
                        state.data.forEach {
                            item {
                                DirectoryListItem(
                                    directoryName = it.name,
                                    isSelected = it.isSelected,
                                    openSubDirectory = { directoriesViewModel.getSubDirectories(it.path) },
                                    onDirectorySelected = { isSelected -> directoriesViewModel.onDirectorySelected(it.path, isSelected) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}