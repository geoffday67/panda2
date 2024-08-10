package uk.co.sullenart.panda2

import com.google.firebase.messaging.FirebaseMessagingService
import timber.log.Timber

class MessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Timber.d("New token $token")
    }
}