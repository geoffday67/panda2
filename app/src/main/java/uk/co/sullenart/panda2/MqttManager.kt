package uk.co.sullenart.panda2

import android.util.Log
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.exceptions.MqttClientStateException
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.future.await
import java.nio.charset.Charset

class MqttManager(
    server: String,
    port: Int = 1883,
    identifier: String = "",
) {
    private val client: Mqtt3AsyncClient = Mqtt3Client.builder()
        .serverHost(server)
        .serverPort(port)
        .identifier(identifier)
        .buildAsync()

    suspend fun connect() {
        try {
            client.connect().await()
        } catch (ignore: MqttClientStateException) {
            // Ignore this as we're already connected.
        }
    }

    suspend fun disconnect() {
        client.disconnect().await()
    }

    suspend fun publish(topic: String, content: String, retain: Boolean = false) {
        client.publishWith()
            .topic(topic)
            .payload(content.toByteArray())
            .retain(retain)
            .qos(MqttQos.AT_LEAST_ONCE)
            .send()
            .await()
    }

    fun subscribe(topic: String): Flow<String> = callbackFlow {
        Log.d("Panda", "Subscribing to $topic")
        client.subscribeWith()
            .topicFilter(topic)
            .callback {
                trySendBlocking(it.payloadAsBytes.toString(Charset.defaultCharset()))
            }
            .send()
        awaitClose {
            Log.d("Panda", "Closing subscription to $topic")
            client.unsubscribeWith()
                .topicFilter(topic)
                .send()
        }
    }
}