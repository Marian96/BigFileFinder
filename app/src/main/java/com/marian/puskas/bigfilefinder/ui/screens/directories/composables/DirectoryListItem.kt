package com.marian.puskas.bigfilefinder.ui.screens.directories.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bigfilefinder.ui.Icons

@Composable
fun DirectoryListItem(
    directoryName: String,
    isSelected: Boolean,
    openSubDirectory: () -> Unit,
    onDirectorySelected: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { openSubDirectory() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(id = Icons.Folder), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = directoryName)
            Checkbox(checked = isSelected, onCheckedChange = {
                onDirectorySelected(!isSelected)
            })
        }
        Icon(painter = painterResource(id = Icons.ForwardArrow), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    }
    HorizontalDivider()
}