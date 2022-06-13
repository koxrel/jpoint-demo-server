package com.app.service

import com.app.dto.CalculationResponse
import com.app.dto.SuccessfulCalculationResponse
import com.app.dto.UnsuccessfulCalculationReponse
import com.app.model.InsuranceCompany
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Singleton
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import org.slf4j.LoggerFactory


@Singleton
class CalculationService(
    private val networkService: InsuranceNetworkService, private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun getCalculation(insuranceCompany: InsuranceCompany): CalculationResponse {
        val response = networkService.getCalculation(insuranceCompany)

        return objectMapper.readValue<SuccessfulCalculationResponse>(response)
    }

    suspend fun getAllCalculations(): List<CalculationResponse> {
        return InsuranceCompany.values().map { getCalculation(it) }
    }

    suspend fun getAllCalculationsInParallel(): List<CalculationResponse> = coroutineScope {
        val jobs = InsuranceCompany.values().map { async { getCalculation(it) } }.toTypedArray()

        awaitAll(*jobs)
    }

    suspend fun getAllCalculations(channel: SendChannel<CalculationResponse>) = coroutineScope {
        InsuranceCompany.values().forEach { insuranceCompany ->
            launch {
                val result = getCalculation(insuranceCompany)
                channel.send(result)
            }
        }
    }

    suspend fun getAllCalculationsWithErrorPlaceholders(channel: SendChannel<CalculationResponse>) = supervisorScope {
        InsuranceCompany.values().forEach { insuranceCompany ->
            val context = CoroutineExceptionHandler { _, exception ->
                log.error("Could not process calculation for $insuranceCompany", exception)
            }

            launch(context) {
                val result = try {
                    getCalculation(insuranceCompany)
                } catch (e: Exception) {
                    UnsuccessfulCalculationReponse(insuranceCompany)
                }

                channel.send(result)
            }
        }
    }
}
