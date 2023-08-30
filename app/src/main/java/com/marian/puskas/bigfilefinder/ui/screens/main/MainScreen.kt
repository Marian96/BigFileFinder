package com.marian.puskas.bigfilefinder.ui.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import com.marian.puskas.bigfilefinder.R
import com.example.bigfilefinder.ui.Icons

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    numberOfFiles: MutableState<String>,
    onChooseDirectoryClick: () -> Unit,
    searchFiles: () -> Unit
) {
    val showAlertForNumberOfFiles = remember { mutableStateOf(false) }
    val showAlertForSelectedDirectories = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = numberOfFiles.value,
            onValueChange = {
                if (it.isDigitsOnly()) {
                    numberOfFiles.value = it
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(text = stringResource(id = R.string.number_of_files)) }
        )
        Row(modifier = Modifier
            .clickable { onChooseDirectoryClick() }
            .padding(10.dp)) {
            Text(modifier = Modifier.weight(1f), text = stringResource(id = R.string.choose_directories))
            Icon(painter = painterResource(id = Icons.ForwardArrow), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            onClick = {
                if (numberOfFiles.value.isEmpty()) {
                    showAlertForNumberOfFiles.value = true
                } else if (!mainViewModel.hasSelectedDirectories()) {
                    showAlertForSelectedDirectories.value = true
                } else {
                    searchFiles()
                }
            },
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(
                start = 10.dp,
                end = 10.dp,
                top = ButtonDefaults.ContentPadding.calculateTopPadding(),
                bottom = ButtonDefaults.ContentPadding.calculateBottomPadding()
            )
        ) {
            Icon(painter = painterResource(id = Icons.Search), contentDescription = null)
            Text(text = stringResource(id = R.string.search_files), textAlign = TextAlign.Center)
        }
        if (showAlertForNumberOfFiles.value) {
            ShowAlertDialog(
                openDialog = showAlertForNumberOfFiles,
                message = stringResource(id = R.string.number_of_files_error)
            )
        }
        if (showAlertForSelectedDirectories.value) {
            ShowAlertDialog(
                openDialog = showAlertForSelectedDirectories,
                message = stringResource(id = R.string.selected_directories_error)
            )
        }
    }
}

@Composable
fun ShowAlertDialog(
    openDialog: MutableState<Boolean>,
    message: String,
) {
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            TextButton(
                onClick = {
                    openDialog.value = false
                }
            ) {
                Text(stringResource(id = R.string.ok))
            }
        },
        text = { Text(text = message, color = MaterialTheme.colorScheme.error) }
    )
}

