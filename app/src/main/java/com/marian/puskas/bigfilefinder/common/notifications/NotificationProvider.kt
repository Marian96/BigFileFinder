package com.marian.puskas.bigfilefinder.common.notifications

import com.marian.puskas.bigfilefinder.common.notifications.model.NotificationMessage
import kotlinx.coroutines.flow.Flow

interface NotificationProvider {
    fun sendNotification(notification: NotificationMessage)
    fun observeNotificationToSend(): Flow<NotificationMessage>
}