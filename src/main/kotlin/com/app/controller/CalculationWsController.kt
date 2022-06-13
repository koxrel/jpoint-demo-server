package com.app.controller

import com.app.dto.CalculationResponse
import com.app.service.CalculationService
import io.micronaut.websocket.WebSocketSession
import io.micronaut.websocket.annotation.OnMessage
import io.micronaut.websocket.annotation.ServerWebSocket
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@ServerWebSocket("/ws/calculation")
class CalculationWsController(private val calculationService: CalculationService) {
    @OnMessage
    suspend fun onMessage(message: String, session: WebSocketSession) {
        val channel = Channel<CalculationResponse>(capacity = Channel.BUFFERED)

        coroutineScope {
            launch {
                sendMessage(session, channel)
            }
            calculationService.getAllCalculationsWithErrorPlaceholders(channel)

            channel.close()
        }
    }

    private suspend fun sendMessage(session: WebSocketSession, channel: ReceiveChannel<CalculationResponse>) {
        for (message in channel) {
            session.sendAsync(message)
        }
    }
}
