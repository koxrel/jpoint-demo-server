package com.app.controller

import com.app.dto.CalculationResponse
import com.app.service.WsCalculationService
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.websocket.WebSocketSession
import io.micronaut.websocket.annotation.OnMessage
import io.micronaut.websocket.annotation.ServerWebSocket
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@RequestScope
@ServerWebSocket("/ws/calculation")
class CalculationWsController(private val calculationService: WsCalculationService) {

    @OnMessage
    suspend fun onMessage(message: String, session: WebSocketSession) {
        val channel = Channel<CalculationResponse>(capacity = Channel.BUFFERED)

        coroutineScope {
            launch {
                sendMessage(session, channel)
            }

            calculationService.getAllCalculations(channel)

            channel.close()

            session.close()
        }
    }

    suspend fun sendMessage(session: WebSocketSession, channel: ReceiveChannel<CalculationResponse>) {
        for (message in channel) {
            session.sendAsync(message)
        }
    }
}
