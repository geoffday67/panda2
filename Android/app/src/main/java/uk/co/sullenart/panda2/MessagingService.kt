package uk.co.sullenart.panda2

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class MessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Timber.d("New token $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Timber.d("Message received from ${message.from}")
    }
}