package com.insurance.controller

import com.app.dto.CalculationResponse
import com.app.model.InsuranceCompany
import com.insurance.conf.InsuranceDelays
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import kotlinx.coroutines.delay
import kotlin.random.Random

@Controller("/rest/insurance")
class InsuranceController(private val insuranceDelays: InsuranceDelays) {

    @Get("/first")
    suspend fun getFirstInsuranceCompanyCalculation(): CalculationResponse {
        delay(insuranceDelays.first)
        return CalculationResponse(generatePrice(), InsuranceCompany.FIRST)
    }

    @Get("/second")
    suspend fun getSecondInsuranceCompanyCalculation(): CalculationResponse {
        delay(insuranceDelays.second)
        return CalculationResponse(generatePrice(), InsuranceCompany.SECOND)
    }

    @Get("/third")
    suspend fun getThirdInsuranceCompanyCalculation(): CalculationResponse {
        delay(insuranceDelays.third)
        return CalculationResponse(generatePrice(), InsuranceCompany.THIRD)
    }

    private fun generatePrice() = Random.nextInt(3_000, 9_000).toBigDecimal()
}
