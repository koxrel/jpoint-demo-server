package com.app.service

import com.app.dto.CalculationResponse
import com.app.model.InsuranceCompany
import jakarta.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import java.math.BigDecimal
import kotlin.random.Random

interface ICalculationService {
    suspend fun getCalculation(insuranceCompany: InsuranceCompany): CalculationResponse
    suspend fun getAllCalculations(): List<CalculationResponse>
    suspend fun getAllCalculationsInParallel(): List<CalculationResponse>
}

@Singleton
class CalculationService : ICalculationService {
    override suspend fun getCalculation(insuranceCompany: InsuranceCompany): CalculationResponse {
        val price = when (insuranceCompany) {
            InsuranceCompany.FIRST -> {
                Random.nextInt(3_000, 8_000)
            }
            InsuranceCompany.SECOND -> {
                Random.nextInt(3_500, 7_000)
            }
            InsuranceCompany.THIRD -> {
                Random.nextInt(4_000, 9_000)
            }
        }.let(::BigDecimal)

        delay(Random.nextLong(1_000, 8_000))

        return CalculationResponse(price, insuranceCompany)
    }

    override suspend fun getAllCalculations(): List<CalculationResponse> {
        return InsuranceCompany.values().map { getCalculation(it) }
    }

    override suspend fun getAllCalculationsInParallel(): List<CalculationResponse> = coroutineScope {
        val jobs = InsuranceCompany.values().map { async { getCalculation(it) } }.toTypedArray()

        awaitAll(*jobs)
    }
}
