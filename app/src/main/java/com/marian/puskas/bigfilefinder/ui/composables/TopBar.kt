package com.marian.puskas.bigfilefinder.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bigfilefinder.ui.Icons

@Composable
fun TopBar(
    title: String,
    onNavigationClick: () -> Unit
) {
    Row(modifier = Modifier.padding(10.dp)) {
        IconButton(
            onClick = onNavigationClick
        ) {
            Icon(
                painter = painterResource(id = Icons.BackButton),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = title, style = MaterialTheme.typography.headlineLarge)
    }

}