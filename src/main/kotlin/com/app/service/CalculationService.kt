package com.app.service

import com.app.dto.CalculationResponse
import com.app.model.InsuranceCompany
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Singleton
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.trySendBlocking
import java.math.BigDecimal

interface CalculationService {
    suspend fun getCalculation(insuranceCompany: InsuranceCompany): CalculationResponse
    suspend fun getAllCalculations(): List<CalculationResponse>
    suspend fun getAllCalculationsInParallel(): List<CalculationResponse>
}

interface WsCalculationService {
    suspend fun getAllCalculations(channel: SendChannel<CalculationResponse>)
    suspend fun getAllCalculationsWithErrorMessages(channel: SendChannel<CalculationResponse>)
}

@Singleton
class CalculationServiceImpl(private val networkService: InsuranceNetworkService, private val objectMapper: ObjectMapper) : CalculationService, WsCalculationService {
    override suspend fun getCalculation(insuranceCompany: InsuranceCompany): CalculationResponse {
        val response = networkService.getCalculation(insuranceCompany)

        return objectMapper.readValue(response)
    }

    override suspend fun getAllCalculations(): List<CalculationResponse> {
        return InsuranceCompany.values().map { getCalculation(it) }
    }

    override suspend fun getAllCalculationsInParallel(): List<CalculationResponse> = coroutineScope {
        val jobs = InsuranceCompany.values().map { async { getCalculation(it) } }.toTypedArray()

        awaitAll(*jobs)
    }

    override suspend fun getAllCalculations(channel: SendChannel<CalculationResponse>) = coroutineScope {
        InsuranceCompany.values().forEach { insuranceCompany ->
            launch {
                val result = getCalculation(insuranceCompany)
                channel.send(result)
            }
        }
    }

    override suspend fun getAllCalculationsWithErrorMessages(channel: SendChannel<CalculationResponse>) = supervisorScope {
        InsuranceCompany.values().forEach { insuranceCompany ->
            val context = CoroutineExceptionHandler { _, exception ->
                channel.trySendBlocking(CalculationResponse(BigDecimal.ZERO, insuranceCompany))
            }

            launch(context) {
                val result = getCalculation(insuranceCompany)
                channel.send(result)
            }
        }
    }
}
