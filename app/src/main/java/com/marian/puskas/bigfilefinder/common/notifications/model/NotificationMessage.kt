package com.marian.puskas.bigfilefinder.common.notifications.model

import android.graphics.drawable.Icon
import com.example.bigfilefinder.ui.Icons

data class NotificationMessage(
    val notificationIcon: Int = Icons.Search,
    val title: String = "",
    val description: String = ""
)