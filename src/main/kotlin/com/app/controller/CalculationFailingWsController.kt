package com.app.controller

import com.app.dto.CalculationResponse
import com.app.service.IWsCalculationService
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.websocket.WebSocketSession
import io.micronaut.websocket.annotation.OnMessage
import io.micronaut.websocket.annotation.ServerWebSocket
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

@RequestScope
@ServerWebSocket("/ws/failing/calculation")
class CalculationFailingWsController(private val calculationService: IWsCalculationService) {

    private var calculationJob: Job? = null

    @OnMessage
    suspend fun onMessage(message: String, session: WebSocketSession) {
        calculationJob?.cancelAndJoin()

        val channel = Channel<CalculationResponse>(capacity = Channel.BUFFERED)

        coroutineScope {
            launch {
                sendMessage(session, channel)
            }

            calculationJob = launch {
                try {
                    calculationService.getAllCalculationsWithErrorMessages(channel)
                }
                finally {
                    channel.close()
                }
            }
        }
    }

    suspend fun sendMessage(session: WebSocketSession, channel: ReceiveChannel<CalculationResponse>) {
        for (message in channel) {
            session.sendAsync(message)
        }
    }
}
