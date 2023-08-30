package com.marian.puskas.bigfilefinder.common.notifications

import com.marian.puskas.bigfilefinder.common.notifications.model.NotificationMessage
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class NotificationProviderImpl @Inject constructor(): NotificationProvider {
    private val notificationFlow = MutableSharedFlow<NotificationMessage>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun sendNotification(notification: NotificationMessage) {
        notificationFlow.tryEmit(notification)
    }

    override fun observeNotificationToSend(): Flow<NotificationMessage> = notificationFlow
}