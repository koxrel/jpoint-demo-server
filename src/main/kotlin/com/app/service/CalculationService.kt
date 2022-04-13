package com.app.service

import com.app.dto.CalculationResponse
import com.app.model.InsuranceCompany
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

interface CalculationService {
    suspend fun getCalculation(insuranceCompany: InsuranceCompany): CalculationResponse
    suspend fun getAllCalculations(): List<CalculationResponse>
    suspend fun getAllCalculationsInParallel(): List<CalculationResponse>
}

@Singleton
class CalculationServiceImpl(private val networkService: InsuranceNetworkService, private val objectMapper: ObjectMapper) : CalculationService {
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
}
