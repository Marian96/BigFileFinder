package com.marian.puskas.bigfilefinder.ui.screens.directories.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bigfilefinder.ui.Icons

@Composable
fun ParentDirectory(
    modifier: Modifier = Modifier,
    isRootDirectory: Boolean,
    isClickable: Boolean,
    parentDirectoryName: String = "",
    onParentDirectoryClick: () -> Unit
) {
    if (isRootDirectory) {
        AssistChip(
            onClick = { onParentDirectoryClick() },
            label = { Text(text = parentDirectoryName) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = Icons.Home),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            }
        )
    } else {
        Row(modifier = modifier.padding(horizontal = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp),) {
            Icon(
                painter = painterResource(id = Icons.RightArrow),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null
            )
            AssistChip(
                onClick = { if (isClickable) onParentDirectoryClick() },
                label = { Text(text = parentDirectoryName) },
            )
        }
    }
}